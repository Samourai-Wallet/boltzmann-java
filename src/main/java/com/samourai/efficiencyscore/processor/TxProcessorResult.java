package com.samourai.efficiencyscore.processor;

import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.linker.IntraFees;
import com.samourai.efficiencyscore.linker.TxosLinkerResult;

import java.util.Set;

public class TxProcessorResult extends TxosLinkerResult {

    private int fees;
    private IntraFees intraFees;
    private Double efficiency;

    public TxProcessorResult(int nbCmbn, int[][] matLnk, Set<int[]> dtrmLnks, Txos txos, int fees, IntraFees intraFees, Double efficiency) {
        super(nbCmbn, matLnk, dtrmLnks, txos);
        this.fees = fees;
        this.intraFees = intraFees;
        this.efficiency = efficiency;
    }

    public int getFees() {
        return fees;
    }

    public IntraFees getIntraFees() {
        return intraFees;
    }

    public Double getEfficiency() {
        return efficiency;
    }
}
