package com.samourai.efficiencyscore.linker;

import com.samourai.efficiencyscore.aggregator.TxosAggregates;
import com.samourai.efficiencyscore.aggregator.TxosAggregatesMatches;
import com.samourai.efficiencyscore.aggregator.TxosAggregator;
import com.samourai.efficiencyscore.aggregator.TxosAggregatorResult;
import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.processor.TxProcessorConst;
import com.samourai.efficiencyscore.utils.ListsUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private static final String MARKER_PACK = "PACK_I";

    private Txos txos;

    // fees associated to the transaction
    private int feesOrig;
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
        this.feesOrig = fees;
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
            txos = packLinkedTxos(linkedTxos, txos); // TODO test + verif txos vs packedTxos
        }

        // Manages fees
        if (options.contains(TxosLinkerOptionEnum.MERGE_FEES) && this.feesOrig > 0) {
            // Manages fees as an additional output (case of sharedsend by blockchain.info).
            // Allows to reduce the volume of computations to be done.
            this.fees = 0;
            this.txos.getOutputs().put(MARKER_FEES, this.feesOrig);
        }
        else {
            this.fees = this.feesOrig;
        }

        TxosAggregator aggregator = new TxosAggregator();

        int nbOuts = txos.getOutputs().size();
        int nbIns = txos.getInputs().size();
        boolean hasIntraFees = intraFees != null && intraFees.hasFees();

        // Checks deterministic links
        int nbCmbn = 0;
        int[][] matLnk = new int[nbOuts][nbIns];
        Set<int[]> dtrmLnks = new LinkedHashSet<>();
        if (options.contains(TxosLinkerOptionEnum.PRECHECK) && this.checkLimitOk() && !hasIntraFees) {

            // Prepares the data
            TxosAggregates allAgg = aggregator.prepareData(txos);
            txos = new Txos(allAgg.getInAgg().getTxos(), allAgg.getOutAgg().getTxos());
            TxosAggregatesMatches aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees);

            // Checks deterministic links
            dtrmLnks = aggregator.checkDtrmLinks(txos, allAgg, aggMatches);

            // If deterministic links have been found, fills the linkability matrix
            // (returned as result if linkability is not processed)
            if (!dtrmLnks.isEmpty()) {
                final int[][] matLnkFinal = matLnk;
                dtrmLnks.forEach(dtrmLnk ->
                    matLnkFinal[dtrmLnk[0]][dtrmLnk[1]] = 1
                );
            }
        }

        // Checks if all inputs and outputs have already been merged
        if (nbIns == 0 || nbOuts == 0) {
            nbCmbn = 1;
            for (int[] line : matLnk) {
                Arrays.fill(line, 1);
            }
        }
        else if(options.contains(TxosLinkerOptionEnum.LINKABILITY) && this.checkLimitOk()) {
            // Packs deterministic links if needed
            if (!dtrmLnks.isEmpty()) {

                List<Set<String>> dtrmCoordsList = dtrmLnks.stream().map(array -> {
                    Set<String> set = new LinkedHashSet<>();
                    set.add(""+txos.getOutputs().keySet().toArray()[array[0]]);
                    set.add(""+txos.getInputs().keySet().toArray()[array[1]]);
                    return set;
                }).collect(Collectors.toList());
                txos = packLinkedTxos(dtrmCoordsList, txos);
            }

            // Prepares data
            TxosAggregates allAgg = aggregator.prepareData(txos);
            txos = new Txos(allAgg.getInAgg().getTxos(), allAgg.getOutAgg().getTxos());
            TxosAggregatesMatches aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees);

            // Computes a matrix storing a tree composed of valid pairs of input aggregates
            Map<Integer,List<int[]>> matInAggCmbn = aggregator.computeInAggCmbn(aggMatches);

            // Builds the linkability matrix
            TxosAggregatorResult result = aggregator.computeLinkMatrix(txos, allAgg, aggMatches, matInAggCmbn, maxDuration);
            nbCmbn = result.getNbCmbn();
            matLnk = result.getMatLnk();

            // Refresh deterministical links
            dtrmLnks = aggregator.findDtrmLinks(matLnk, nbCmbn);
        }

        // Unpacks the matrix
        UnpackLinkMatrixResult unpackResult = unpackLinkMatrix(matLnk, nbCmbn, txos);
        txos = unpackResult.getTxos();
        matLnk = unpackResult.getMatLnk();

        return new TxosLinkerResult(nbCmbn, matLnk, dtrmLnks, txos);
    }

    private enum PackType {
        INPUTS
    }

    private class Pack {
        private String lbl;
        private int valIns;
        private PackType packType;
        private List<AbstractMap.SimpleEntry<String, Integer>> ins;
        private List<String> outs;

        Pack(String lbl, int valIns, PackType packType, List<AbstractMap.SimpleEntry<String, Integer>> ins, List<String> outs) {
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

        public List<AbstractMap.SimpleEntry<String, Integer>> getIns() {
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

        Txos packedTxos = new Txos(new LinkedHashMap<>(txos.getInputs()), new LinkedHashMap<>(txos.getOutputs()));

        // Merges packs sharing common elements
        List<Set<String>> newPacks = ListsUtils.mergeSets(linkedTxos);

        for (Set<String> pack : newPacks) {
            List<AbstractMap.SimpleEntry<String, Integer>> ins = new ArrayList<>();
            int valIns = 0;

            for (String inPack : pack) {
                if (inPack.startsWith(TxProcessorConst.MARKER_INPUT)) {
                    int inPackValue = packedTxos.getInputs().get(inPack);
                    ins.add(new AbstractMap.SimpleEntry<>(inPack, inPackValue));
                    valIns += packedTxos.getInputs().get(inPack);
                    packedTxos.getInputs().remove(inPack);
                }
            }
            idx++;

            if (!ins.isEmpty()) {
                String lbl = MARKER_PACK+idx;
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
    private UnpackLinkMatrixResult unpackLinkMatrix(int[][] matLnk, int nbCmbn, Txos txos) {
        int[][] matRes = matLnk; //TODO clone
        nbCmbn = Math.max(1, nbCmbn);

        final Txos newTxos = new Txos(new LinkedHashMap<>(txos.getInputs()), new LinkedHashMap<>(txos.getOutputs()));

        packs.stream()/*.sorted(Collections.reverseOrder())*/.forEach(pack -> { // TODO reverse order
            if (PackType.INPUTS.equals(pack.getPackType())) {

                int idx = unpackTxos(new LinkedHashMap<>(newTxos.getInputs()), newTxos.getInputs(), pack);

                if (matLnk != null) {
                    int nbIns = pack.getIns().size();
                    int nbOuts = txos.getOutputs().size();

                    // TODO IMPLEMENT unpack matrix
                    /*int[][] newMatLnk = new int[nbOuts][nbIns];
                    IntStream.range(0, nbOuts).map(i ->
                        IntStream.range(0, nbIns).map(j -> {
                            if (j <= idx) {
                                return matLnk[i][j];
                            }
                            else if (j >= (idx+nbIns)) {
                                return matLnk[i][j-idx];
                            }
                            else {
                                return pack.getIns().get(j-idx);
                            }
                        })
                    );*/
                }

            }
        });

        return new UnpackLinkMatrixResult(newTxos, matRes);
    }

    /**
     * Unpack txos.
     * @param currentTxos packed txos containing the pack
     * @param newTxos map to return unpacked txos
     * @param pack pack to unpack
     * @return idx pack indice in txos
     */
    private int unpackTxos(Map<String,Integer> currentTxos, Map<String,Integer> newTxos, Pack pack) {
        // find pack indice in txos
        String[] txoKeys = currentTxos.keySet().toArray(new String[]{});
        int idx = IntStream.range(0, txoKeys.length).filter(i -> pack.getLbl().equals(txoKeys[i])).findFirst().getAsInt();

        newTxos.clear();
        Iterator<Map.Entry<String,Integer>> currentTxosIterator = currentTxos.entrySet().iterator();
        // keep txos before pack
        for (int i=0; i<idx; i++) {
            Map.Entry<String,Integer> entry = currentTxosIterator.next();
            newTxos.put(entry.getKey(), entry.getValue());
        }
        // insert packed txos
        for (Map.Entry<String,Integer> entry : pack.getIns()) {
            newTxos.put(entry.getKey(), entry.getValue());
        }
        currentTxosIterator.next(); // skip packed txo
        // keep txos after pack
        while (currentTxosIterator.hasNext()) {
            Map.Entry<String,Integer> entry = currentTxosIterator.next();
            newTxos.put(entry.getKey(), entry.getValue());
        }
        return idx;
    }

    // LIMITS
    private boolean checkLimitOk() {
        int lenIn = txos.getInputs().size();
        int lenOut = txos.getOutputs().size();
        int maxCard = Math.max(lenIn, lenOut);
        return (maxCard <= maxTxos);
    }
}
