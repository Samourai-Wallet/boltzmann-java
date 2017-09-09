package com.samourai.efficiencyscore.aggregator;

public class TxosAggregatesData {

    private int[][] allAggIndexes; // each entry value contains array of txos indexes for corresponding allAggVal[entry.key]
    private int[] allAggVal;

    public TxosAggregatesData(int[][] allAggIndexes, int[] allAggVal) {
        this.allAggIndexes = allAggIndexes;
        this.allAggVal = allAggVal;
    }

    public int[][] getAllAggIndexes() {
        return allAggIndexes;
    }

    public int[] getAllAggVal() {
        return allAggVal;
    }
}
