package com.samourai.boltzmann.linker;

import com.samourai.boltzmann.aggregator.TxosAggregatorResult;
import com.samourai.boltzmann.beans.Txos;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.Set;

public class TxosLinkerResult extends TxosAggregatorResult {

  private Set<long[]> dtrmLnksById;
  private Txos txos;

  public TxosLinkerResult(
      int nbCmbn, ObjectBigList<IntBigList> matLnk, Set<long[]> dtrmLnksById, Txos txos) {
    super(nbCmbn, matLnk);
    this.dtrmLnksById = dtrmLnksById;
    this.txos = txos;
  }

  public Set<long[]> getDtrmLnksById() {
    return dtrmLnksById;
  }

  public Txos getTxos() {
    return txos;
  }
}
