package com.samourai.efficiencyscore.processor;

import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.linker.IntraFees;
import com.samourai.efficiencyscore.linker.TxosLinkerResult;

import java.util.Arrays;
import java.util.Set;

public class TxProcessorResult extends TxosLinkerResult {

    private int fees;
    private IntraFees intraFees;
    private Double efficiency;
    private double[][] matLnkProbabilities;

    public TxProcessorResult(int nbCmbn, int[][] matLnkCombinations, double[][] matLnkProbabilities, Set<int[]> dtrmLnks, Txos txos, int fees, IntraFees intraFees, Double efficiency) {
        super(nbCmbn, matLnkCombinations, dtrmLnks, txos);
        this.matLnkProbabilities = matLnkProbabilities;
        this.fees = fees;
        this.intraFees = intraFees;
        this.efficiency = efficiency;
    }

    public double[][] getMatLnkProbabilities() {
        return matLnkProbabilities;
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
