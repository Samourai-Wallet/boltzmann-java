package com.samourai.boltzmann.processor;

import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.linker.IntraFees;
import com.samourai.boltzmann.linker.TxosLinkerResult;
import java.util.Set;

public class TxProcessorResult extends TxosLinkerResult {

  private double[][] matLnkProbabilities;
  private Double entropy;
  private long fees;
  private IntraFees intraFees;
  private Double efficiency;

  public TxProcessorResult(
          int nbCmbn,
          int[][] matLnkCombinations,
          double[][] matLnkProbabilities,
          Double entropy,
          Set<int[]> dtrmLnksById,
          Txos txos,
          long fees,
          IntraFees intraFees,
          Double efficiency) {
    super(nbCmbn, matLnkCombinations, dtrmLnksById, txos);
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

  public int getNbLinks() {
    return getTxos().getInputs().size() * getTxos().getOutputs().size();
  }

  public int getNbDL() {
    return getDtrmLnksById().size();
  }

  public Double getRatioDL() {
    return ((double)getNbDL()) / (double)getNbLinks();
  }

  public Double getNRatioDL() {
    return 1.0 - getRatioDL();
  }

}
