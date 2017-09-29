package com.samourai.efficiencyscore.aggregator;

import java.util.Map;

public class TxosAggregatesData {

    private Map<String, Integer> txos;
    private int[][] allAggIndexes; // each entry value contains array of txos indexes for corresponding allAggVal[entry.key]
    private int[] allAggVal;

    public TxosAggregatesData(Map<String, Integer> txos, int[][] allAggIndexes, int[] allAggVal) {
        this.txos = txos;
        this.allAggIndexes = allAggIndexes;
        this.allAggVal = allAggVal;
    }

    public Map<String, Integer> getTxos() {
        return txos;
    }

    public int[][] getAllAggIndexes() {
        return allAggIndexes;
    }

    public int[] getAllAggVal() {
        return allAggVal;
    }
}
