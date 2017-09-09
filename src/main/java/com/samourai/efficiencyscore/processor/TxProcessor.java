package com.samourai.efficiencyscore.processor;

import com.samourai.efficiencyscore.beans.Tx;
import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.linker.*;
import com.samourai.efficiencyscore.utils.ListsUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TxProcessor {

    public TxProcessor() {
    }

    /**
     * Processes a transaction
     * @param tx Transaction to be processed
     * @param options options to be applied during processing
     * @param maxDuration max duration allocated to processing of a single tx (in seconds)
     * @param maxTxos max number of txos. Txs with more than max_txos inputs or outputs are not processed.
     * @param maxCjIntrafeesRatio max intrafees paid by the taker of a coinjoined transaction. Expressed as a percentage of the coinjoined amount.
     * @return TxProcessorResult
     */
    public TxProcessorResult processTx(Tx tx, TxosLinkerOptionEnum[] options, int maxDuration, int maxTxos, int maxCjIntrafeesRatio) {

        long t1 = System.currentTimeMillis();

        // Builds lists of filtered input/output txos (with generated ids)
        FilteredTxos filteredIns = filterTxos(tx.getTxos().getInputs(), 'I');
        FilteredTxos filteredOuts = filterTxos(tx.getTxos().getOutputs(), 'O');

        // Computes total input & output amounts + fees
        int sumInputs = filteredIns.getTxos().values().stream().mapToInt(x -> x).sum();
        int sumOutputs = filteredOuts.getTxos().values().stream().mapToInt(x -> x).sum();
        int fees = sumInputs - sumOutputs;

        // Sets default intrafees paid by participants (fee_received_by_maker, fees_paid_by_taker)
        IntraFees intraFees = new IntraFees(0, 0);

        TxosLinkerResult result;

        // Processes the transaction
        if (filteredIns.getTxos().size() <= 1 || filteredOuts.getTxos().size() == 1) {
            // Txs having no input (coinbase) or only 1 input/output (null entropy)
            // When entropy = 0, all inputs and outputs are linked and matrix is filled with 1.
            // No need to build this matrix. Every caller should be able to manage that.
            result = new TxosLinkerResult(1, null, null, new Txos(filteredIns.getTxos(), filteredOuts.getTxos()));
        }
        else {
            // Initializes the TxosLinker for this tx
            Txos filteredTxos = new Txos(filteredIns.getTxos(), filteredOuts.getTxos());
            TxosLinker linker = new TxosLinker(filteredTxos, fees, maxDuration, maxTxos);

            // Computes a list of sets of inputs controlled by a same address
            Collection<Set<String>> linkedIns = new ArrayList<>();//linked_ins = getLinkedTxos(filtered_ins, map_ins) if ('MERGE_INPUTS' in options) else [] //TODO V2
            // Computes a list of sets of outputs controlled by a same address (not recommended)
            Collection<Set<String>> linkedOuts = new ArrayList<>();//linked_outs = get_linked_txos(filtered_outs, map_outs) if ('MERGE_OUTPUTS' in options) else [] //TODO V2

            // Computes intrafees to be used during processing
            if (maxCjIntrafeesRatio > 0) {
                // Computes a theoretic max number of participants
                Collection<Set<String>> lsFilteredIns = filteredIns.getTxos().keySet().stream().map(txoId -> {
                    Set<String> set = new HashSet<>();
                    set.add(txoId);
                    return set;
                }).collect(Collectors.toList());

                Collection<Set<String>> insToMerge = new ArrayList<>();
                insToMerge.addAll(lsFilteredIns);
                insToMerge.addAll(linkedIns);
                int maxNbPtcpts = ListsUtils.mergeSets(insToMerge).size();

                // Checks if tx has a coinjoin pattern + gets estimated number of participants and coinjoined amount
                boolean isCj = false; // is_cj, nb_ptcpts, cj_amount = check_coinjoin_pattern(filtered_ins, filtered_outs, max_nb_ptcpts) // TODO IMPLEMENT

                // If coinjoin pattern detected, computes theoretic max intrafees
                if (isCj) {
                    // intrafees = compute_coinjoin_intrafees(nb_ptcpts, cj_amount, max_cj_intrafees_ratio) // TODO IMPLEMENT
                }
            }

            // Computes entropy of the tx and txos linkability matrix
            Collection<Set<String>> linkedTxos = new ArrayList<>();
            linkedTxos.addAll(linkedIns);
            linkedTxos.addAll(linkedOuts);
            result = linker.process(linkedTxos, options, intraFees);
        }

        // Computes tx efficiency (expressed as the ratio: nb_cmbn/nb_cmbn_perfect_cj)
        Double efficiency = computeWalletEfficiency(filteredIns.getTxos().size(), filteredOuts.getTxos().size(), result.getNbCmbn());

        // Post processes results (replaces txo ids by bitcoin addresses)

        Map<String, Integer> txoIns = postProcessTxos(result.getTxos().getInputs(), filteredIns.getMapIdAddr());
        Map<String, Integer> txoOuts = postProcessTxos(result.getTxos().getOutputs(), filteredOuts.getMapIdAddr());
        return new TxProcessorResult(result.getNbCmbn(), result.getMatLnk(), result.getDtrmLnks(), new Txos(txoIns, txoOuts), fees, intraFees, efficiency);
    }

    /**
     * Filters a list of txos by removing txos with null value (OP_RETURN, ...).
     * Defines an id for each txo
     * @param txos list of Txo objects
     * @param prefix a prefix to be used for ids generated
     * @return FilteredTxos
     */
    private FilteredTxos filterTxos(Map<String, Integer> txos, char prefix) {
        Map<String, Integer> filteredTxos = new HashMap<>();
        Map<String, String> mapIdAddr = new HashMap<>();

        txos.entrySet().forEach(entry -> {
            if (entry.getValue() > 0) {
                String txoId = Character.toString(prefix)+mapIdAddr.size();
                filteredTxos.put(txoId, entry.getValue());
                mapIdAddr.put(txoId, entry.getKey());
            }
        });

        return new FilteredTxos(filteredTxos, mapIdAddr);
    }

    /**
     * Post processes a list of txos
     Basically replaces txo_id by associated bitcoin address
     Returns a list of txos (tuples (address, amount))
     * @param txos list of txos (tuples (txo_id, amount))
     * @param mapIdAddr mapping txo_ids to addresses
     */
    public Map<String, Integer> postProcessTxos(Map<String, Integer> txos, Map<String,String> mapIdAddr) {
        return txos.entrySet().stream().map(entry -> {
            if (entry.getKey().startsWith("I") || entry.getKey().startsWith("O")) { // TODO constans
                return new AbstractMap.SimpleEntry<>(mapIdAddr.get(entry.getKey()), entry.getValue());
            }
            return entry; // PACKS, FEES...
        }).collect(Collectors.toMap(entry -> entry.getKey(),entry -> entry.getValue()));
    }

    /**
     * Computes the efficiency of a transaction defined by:
     - its number of inputs
     - its number of outputs
     - its entropy (expressed as number of combinations)
     * @param nbIns number of inputs
     * @param nbOuts number of outputs
     * @param nbCmbn number of combinations found for the transaction
     * @return an efficiency score computed as the ratio: nb_cmbn / nb_cmbn_closest_perfect_coinjoin
     */
    private Double computeWalletEfficiency(int nbIns, int nbOuts, int nbCmbn) {
        if (nbCmbn == 1) {
            return 0.0;
        }

        NbTxos tgtNbTxos = getClosestPerfectCoinjoin(nbIns, nbOuts);
        Double nbCmbnPrfctCj = computeCmbnsPerfectCj(tgtNbTxos.getNbIns(), tgtNbTxos.getNbOuts());
        if (nbCmbnPrfctCj == null) {
            return null;
        }
        return nbCmbn / nbCmbnPrfctCj;
    }

    /**
     * Computes the structure of the closest perfect coinjoin
     for a transaction defined by its #inputs and #outputs

     A perfect coinjoin is defined as a transaction for which:
     - all inputs have the same amount
     - all outputs have the same amount
     - 0 fee are paid (equiv. to same fee paid by each input)
     - nb_i % nb_o == 0, if nb_i >= nb_o
     or
     nb_o % nb_i == 0, if nb_o >= nb_i

     Returns a tuple (nb_i, nb_o) for the closest perfect coinjoin
     * @param nbIns number of inputs of the transaction
     * @param nbOuts number of outputs of the transaction
     * @return
     */
    private NbTxos getClosestPerfectCoinjoin(int nbIns, int nbOuts) {
        if (nbIns > nbOuts) {
            // Reverses inputs and outputs
            int nbInsInitial = nbIns;
            nbIns = nbOuts;
            nbOuts = nbInsInitial;
        }

        if (nbOuts % nbIns == 0) {
            return new NbTxos(nbIns, nbOuts);
        }
        int tgtRatio = 1 + nbOuts/nbIns;
        return new NbTxos(nbIns, nbIns*tgtRatio);
    }

    /**
     * Computes the number of combinations
     for a perfect coinjoin with nb_i inputs and nb_o outputs.

     A perfect coinjoin is defined as a transaction for which:
     - all inputs have the same amount
     - all outputs have the same amount
     - 0 fee are paid (equiv. to same fee paid by each input)
     - nb_i % nb_o == 0, if nb_i >= nb_o
     or
     nb_o % nb_i == 0, if nb_o >= nb_i

     Notes:
     Since all inputs have the same amount
     we can use exponential Bell polynomials to retrieve
     the number and structure of partitions for the set of inputs.

     Since all outputs have the same amount
     we can use a direct computation of combinations of k outputs among n.

     * @param nbIns number of inputs
     * @param nbOuts number of outputs
     * @return the number of combinations
     */
    private Double computeCmbnsPerfectCj(int nbIns, int nbOuts) { // TODO double
        if (nbIns > nbOuts) {
            // Reverses inputs and outputs
            int nbInsInitial = nbIns;
            nbIns = nbOuts;
            nbOuts = nbInsInitial;
        }

        if (nbOuts % nbIns != 0) {
            return null;
        }

        // Checks if we can use precomputed values
        if (nbIns <= 1 || nbOuts <= 1) {
            return 1.0;
        }
        else if (nbIns <= 20 && nbOuts <= 60) {
            return TxProcessorConst.getNbCmbnPrfctCj(nbIns,nbOuts);
        }

        // Initializes the total number of combinations for the tx
        int nbCmbn = 0;

        // Computes the ratio between #outputs and #inputs
        float ratioOutsIns = nbOuts / nbIns;


        // Iterates over partioning of inputs in k_i parts
        for (int ki=1; ki<nbIns+1; ki++) {
            int partsKi = bell(nbIns, ki);

            int coeffMonoimial = partsKi;
            // TODO IMPLEMENT
        }
        return null; // TODO IMPLEMENT
    }

    // number of partitions of a set with n elements into k subsets
    // Returns count of different partitions of n elements in k subsets
    // http://www.geeksforgeeks.org/count-number-of-ways-to-partition-a-set-into-k-subsets/
    private int bell(int n, int k)
    {
        // Table to store results of subproblems
        int dp[][] = new int[n+1][Math.max(n+1,k+1)];

        // Base cases
        for (int i=0; i<=n; i++)
            dp[i][0] = 0;
        for (int i=0; i<=k; i++)
            dp[0][k] = 0;

        // Fill rest of the entries in dp[][]
        // in bottom up manner
        for (int i=1; i<=n; i++)
            for (int j=1; j<=i; j++)
                if (j == 1 || i == j) {
                    dp[i][j] = 1;}
                else
                    dp[i][j] = j*dp[i-1][j] + dp[i-1][j-1];

        return dp[n][k];
    }


}
