package com.samourai.efficiencyscore.linker;

import com.samourai.efficiencyscore.aggregator.TxosAggregates;
import com.samourai.efficiencyscore.aggregator.TxosAggregatesMatches;
import com.samourai.efficiencyscore.aggregator.TxosAggregator;
import com.samourai.efficiencyscore.aggregator.TxosAggregatorResult;
import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.utils.ListsUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compute the entropy and the inputs/outputs linkability of a of Bitcoin transaction.
 */
public class TxosLinker {
    // Default maximum duration in seconds
    private static final int MAX_DURATION = 180;

    // Max number of inputs (or outputs) which can be processed by this algorithm
    private static final int MAX_NB_TXOS = 12;

    // Markers
    private static final String MARKER_FEES = "FEES";
    public static final String MARKER_PACK = "PACK"; //TODO private

    private Txos txos;

    // IntraFees associated to the transaction
    private int _orig_fees; // TODO?
    private int fees;

    private List<Pack> packs = new ArrayList<>();

    // max number of txos. Txs with more than max_txos inputs or outputs are not processed.
    int maxTxos;

    // Maximum duration of the script (in seconds)
    int maxDuration = MAX_DURATION;

    /**
     * Constructor.
     * @param txos list of inputs/ouputs txos [(v1_id, v1_amount), ...]
     * @param fees amount of fees associated to the transaction
     * @param maxDuration max duration allocated to processing of a single tx (in seconds)
     * @param maxTxos max number of txos. Txs with more than max_txos inputs or outputs are not processed.
     */
    public TxosLinker(Txos txos, int fees, int maxDuration, int maxTxos) {
        this.txos = txos;
        this._orig_fees = fees;
        this.maxDuration = maxDuration;
        this.maxTxos = maxTxos;
    }

    /**
     * Computes the linkability between a set of input txos and a set of output txos.
     * @param linkedTxos list of sets storing linked input txos. Each txo is identified by its id
     * @param options actions to be applied
     * @param intraFees tuple (fees_maker, fees_taker) of max "fees" paid among participants used for joinmarket transactions
                        fees_maker are potential max "fees" received by a participant from another participant
                        fees_taker are potential max "fees" paid by a participant to all others participants
     * @return
     */
    public TxosLinkerResult process(Collection<Set<String>> linkedTxos, Set<TxosLinkerOptionEnum> options, IntraFees intraFees) {
        // Packs txos known as being controlled by a same entity
        // It decreases the entropy and speeds-up computations
        if(linkedTxos!=null && !linkedTxos.isEmpty()) {
            ListsUtils.mergeSets(linkedTxos);
        }

        // Manages fees
        if (options.contains(TxosLinkerOptionEnum.MERGE_FEES) && this._orig_fees > 0) {
            // Manages fees as an additional output (case of sharedsend by blockchain.info).
            // Allows to reduce the volume of computations to be done.
            this.fees = 0;
            this.txos.getOutputs().put(MARKER_FEES, this._orig_fees);
        }
        else {
            this.fees = this._orig_fees;
        }

        TxosAggregator aggregator = new TxosAggregator();

        int nbOuts = txos.getOutputs().size();
        int nbIns = txos.getInputs().size();
        boolean hasIntraFees = intraFees != null && intraFees.hasFees();

        // Checks deterministic links
        int nbCmbn = 0;
        int[][] matLnk = new int[nbOuts][nbIns];
        Set<int[]> dtrmLnks = null;
        if (options.contains(TxosLinkerOptionEnum.PRECHECK) && this.checkLimitOk() && !hasIntraFees) {

            // Prepares the data
            TxosAggregates allAgg = aggregator.prepareData(txos);
            TxosAggregatesMatches aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees);

            // Checks deterministic links
            dtrmLnks = aggregator.checkDtrmLinks(txos, allAgg, aggMatches);

            // If deterministic links have been found, fills the linkability matrix
            // (returned as result if linkability is not processed)
            if (!dtrmLnks.isEmpty()) {
                for (int[] dtrmLnk : dtrmLnks) {
                    matLnk[dtrmLnk[0]][dtrmLnk[1]] = 1;
                }
            }
        }

        Txos packedTxos = txos;

        // Checks if all inputs and outputs have already been merged
        if (nbIns == 0 || nbOuts == 0) {
            nbCmbn = 1;
            for (int[] line : matLnk) {
                Arrays.fill(line, 1);
            }
        }
        else if(options.contains(TxosLinkerOptionEnum.LINKABILITY) && this.checkLimitOk()) {
            // Packs deterministic links if needed
            if (dtrmLnks != null && !dtrmLnks.isEmpty()) {

                List<Set<String>> dtrmCoordsList = dtrmLnks.stream().map(array -> {
                    Set<String> set = new HashSet<>();
                    set.add("O"+array[0]); // TODO constant
                    set.add("I"+array[1]); // TODO constant
                    return set;
                }).collect(Collectors.toList());
                packedTxos = packLinkedTxos(dtrmCoordsList, txos);

                // Prepares data
                TxosAggregates allAgg = aggregator.prepareData(packedTxos); // TODO optimize repetition
                TxosAggregatesMatches aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees); // TODO optimize repetition

                // Computes a matrix storing a tree composed of valid pairs of input aggregates
                Map<Integer,List<int[]>> matInAggCmbn = aggregator.computeInAggCmbn(aggMatches);

                // Builds the linkability matrix
                TxosAggregatorResult result = aggregator.computeLinkMatrix(packedTxos, allAgg, aggMatches, matInAggCmbn, maxDuration);
                nbCmbn = result.getNbCmbn();
                matLnk = result.getMatLnk();
            }
        }

        // Unpacks the matrix
        matLnk = unpackLinkMatrix(matLnk, nbCmbn, packedTxos);

        return new TxosLinkerResult(nbCmbn, matLnk, dtrmLnks, txos);
    }

    private enum PackType {
        INPUTS, OUTPUTS
    }

    private class Pack {
        private String lbl;
        private int valIns;
        private PackType packType;
        private List<String> ins;
        private List<String> outs;

        Pack(String lbl, int valIns, PackType packType, List<String> ins, List<String> outs) {
            this.lbl = lbl;
            this.valIns = valIns;
            this.packType = packType;
            this.ins = ins;
            this.outs = outs;
        }

        public String getLbl() {
            return lbl;
        }

        public int getValIns() {
            return valIns;
        }

        public PackType getPackType() {
            return packType;
        }

        public List<String> getIns() {
            return ins;
        }

        public List<String> getOuts() {
            return outs;
        }
    }

    /**
     * Packs input txos which are known as being controlled by a same entity
     * @param linkedTxos list of sets storing linked input txos. Each txo is identified by its "id"
     * @return Txos
     */
    private Txos packLinkedTxos(Collection<Set<String>> linkedTxos, Txos txos) {
        int idx = packs.size();

        Txos packedTxos = new Txos(new HashMap<>(txos.getInputs()), new HashMap<>(txos.getOutputs()));

        // Merges packs sharing common elements
        List<Set<String>> newPacks = ListsUtils.mergeSets(linkedTxos);

        for (Set<String> pack : newPacks) {
            List<String> ins = new ArrayList<>();
            int valIns = 0;

            for (String inPack : pack) {
                if (inPack.startsWith("I")) {// TODO clean
                    ins.add(inPack);
                    valIns += packedTxos.getInputs().get(inPack);
                    packedTxos.getInputs().remove(inPack);
                }
            }
            idx++;

            if (!ins.isEmpty()) {
                String lbl = MARKER_PACK+"_I"+idx;
                packedTxos.getInputs().put(lbl, valIns);
                packs.add(new Pack(lbl, valIns, PackType.INPUTS, ins, new ArrayList<>()));
            }
        }
        return packedTxos;
    }

    /**
     * Unpacks linked txos in the linkability matrix.
     * @param matLnk linkability matrix to be unpacked
     * @param nbCmbn number of combinations associated to the linkability matrix
     * @return the unpacked matrix
     */
    private int[][] unpackLinkMatrix(int[][] matLnk, int nbCmbn, Txos txos) {
        int[][] matRes = matLnk; //TODO clone
        nbCmbn = Math.max(1, nbCmbn);

        packs.stream().forEach(pack -> {
            if (PackType.INPUTS.equals(pack.getPackType())) {
                int idx=0;
                int i=0;
                for (Iterator<Map.Entry<String,Integer>> iter = txos.getInputs().entrySet().iterator(); iter.hasNext(); iter.next()) { // TODO optimize inputs.index(pack.lbl)
                    if (iter.next().getKey().equals(pack.getLbl())) {
                        idx = i; // TODO VERIFY
                        break;
                    }
                    i++;
                }

                if (matLnk != null) {
                    int nbIns = pack.getIns().size();
                    int nbOuts = pack.getOuts().size();

                    // Inserts columns into the matrix for packed inputs
                    // TODO IMPLEMENT
                }
            }
            else if (PackType.OUTPUTS.equals(pack.getPackType())) {
                int idx=0;
                int i=0;
                for (Iterator<Map.Entry<String,Integer>> iter = txos.getOutputs().entrySet().iterator(); iter.hasNext(); iter.next()) { // TODO optimize inputs.index(pack.lbl)
                    if (iter.next().getKey().equals(pack.getLbl())) {
                        idx = i; // TODO VERIFY
                        break;
                    }
                    i++;
                }

                if (matLnk != null) {
                    int nbIns = pack.getIns().size();
                    int nbOuts = pack.getOuts().size();

                    // Inserts columns into the matrix for packed outputs
                    // TODO IMPLEMENT
                }
            }
        });

        return matRes;
    }


    // LIMITS
    private boolean checkLimitOk() {
        int lenIn = txos.getInputs().size();
        int lenOut = txos.getOutputs().size();
        int maxCard = Math.max(lenIn, lenOut);
        return (maxCard <= maxTxos);
    }
}
