package com.samourai.boltzmann.aggregator;

import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.Map;

public class TxosAggregatesData {

  private Map<String, Long> txos;
  private ObjectBigList<long[]>
      allAggIndexes; // each entry value contains array of txos indexes for corresponding
  // allAggVal[entry.key]
  private long[] allAggVal;

  public TxosAggregatesData(
      Map<String, Long> txos, ObjectBigList<long[]> allAggIndexes, long[] allAggVal) {
    this.txos = txos;
    this.allAggIndexes = allAggIndexes;
    this.allAggVal = allAggVal;
  }

  public Map<String, Long> getTxos() {
    return txos;
  }

  public ObjectBigList<long[]> getAllAggIndexes() {
    return allAggIndexes;
  }

  public long[] getAllAggVal() {
    return allAggVal;
  }
}
