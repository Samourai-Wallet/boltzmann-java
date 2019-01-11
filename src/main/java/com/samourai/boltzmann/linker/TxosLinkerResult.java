package com.samourai.boltzmann.linker;

import com.samourai.boltzmann.aggregator.TxosAggregatorResult;
import com.samourai.boltzmann.beans.Txos;
import java.util.Set;

public class TxosLinkerResult extends TxosAggregatorResult {

  private Set<int[]> dtrmLnksById;
  private Txos txos;

  public TxosLinkerResult(int nbCmbn, int[][] matLnk, Set<int[]> dtrmLnksById, Txos txos) {
    super(nbCmbn, matLnk);
    this.dtrmLnksById = dtrmLnksById;
    this.txos = txos;
  }

  public Set<int[]> getDtrmLnksById() {
    return dtrmLnksById;
  }

  public Txos getTxos() {
    return txos;
  }
}
