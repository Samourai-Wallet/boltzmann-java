package com.samourai.boltzmann.aggregator;

import java.util.List;
import java.util.Map;

public class TxosAggregatesMatches {

  private List<Integer> allMatchInAgg;
  private Map<Integer, Long> matchInAggToVal;
  private Map<Long, List<Integer>> valToMatchOutAgg;

  public TxosAggregatesMatches(
      List<Integer> allMatchInAgg,
      Map<Integer, Long> matchInAggToVal,
      Map<Long, List<Integer>> valToMatchOutAgg) {
    this.allMatchInAgg = allMatchInAgg;
    this.matchInAggToVal = matchInAggToVal;
    this.valToMatchOutAgg = valToMatchOutAgg;
  }

  public List<Integer> getAllMatchInAgg() {
    return allMatchInAgg;
  }

  public Map<Integer, Long> getMatchInAggToVal() {
    return matchInAggToVal;
  }

  public Map<Long, List<Integer>> getValToMatchOutAgg() {
    return valToMatchOutAgg;
  }
}
