package com.samourai.efficiencyscore.processor;

import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.linker.IntraFees;
import com.samourai.efficiencyscore.linker.TxosLinkerResult;

import java.util.Set;

public class TxProcessorResult extends TxosLinkerResult {

    private double[][] matLnkProbabilities;
    private Double entropy;
    private long fees;
    private IntraFees intraFees;
    private Double efficiency;

    public TxProcessorResult(int nbCmbn, int[][] matLnkCombinations, double[][] matLnkProbabilities, Double entropy, Set<int[]> dtrmLnks, Txos txos, long fees, IntraFees intraFees, Double efficiency) {
        super(nbCmbn, matLnkCombinations, dtrmLnks, txos);
        this.matLnkProbabilities = matLnkProbabilities;
        this.entropy = entropy;
        this.fees = fees;
        this.intraFees = intraFees;
        this.efficiency = efficiency;
    }

    public double[][] getMatLnkProbabilities() {
        return matLnkProbabilities;
    }

    public Double getEntropy() {
        return entropy;
    }

    public long getFees() {
        return fees;
    }

    public IntraFees getIntraFees() {
        return intraFees;
    }

    public Double getEfficiency() {
        return efficiency;
    }
}
