package com.samourai.efficiencyscore.aggregator;

import java.util.Map;
import java.util.Set;

public class TxosAggregatesMatches {

    private Set<Integer> allMatchInAgg;
    private Map<Integer, Long> matchInAggToVal;
    private Map<Long, int[]> valToMatchOutAgg;

    public TxosAggregatesMatches(Set<Integer> allMatchInAgg, Map<Integer, Long> matchInAggToVal, Map<Long, int[]> valToMatchOutAgg) {
        this.allMatchInAgg = allMatchInAgg;
        this.matchInAggToVal = matchInAggToVal;
        this.valToMatchOutAgg = valToMatchOutAgg;
    }

    public Set<Integer> getAllMatchInAgg() {
        return allMatchInAgg;
    }

    public Map<Integer, Long> getMatchInAggToVal() {
        return matchInAggToVal;
    }

    public Map<Long, int[]> getValToMatchOutAgg() {
        return valToMatchOutAgg;
    }
}
