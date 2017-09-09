package com.samourai.efficiencyscore.linker;

import com.samourai.efficiencyscore.aggregator.TxosAggregatorResult;
import com.samourai.efficiencyscore.beans.Txos;

import java.util.Set;

public class TxosLinkerResult extends TxosAggregatorResult {

    private Set<int[]> dtrmLnks;
    private Txos txos;

    public TxosLinkerResult(int nbCmbn, int[][] matLnk, Set<int[]> dtrmLnks, Txos txos) {
        super(nbCmbn, matLnk);
        this.dtrmLnks = dtrmLnks;
        this.txos = txos;
    }

    public Set<int[]> getDtrmLnks() {
        return dtrmLnks;
    }

    public Txos getTxos() {
        return txos;
    }
}
