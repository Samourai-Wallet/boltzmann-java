package com.samourai.boltzmann.processor;

import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.linker.IntraFees;
import com.samourai.boltzmann.linker.TxosLinkerResult;
import it.unimi.dsi.fastutil.doubles.DoubleBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.Set;

public class TxProcessorResult extends TxosLinkerResult {

  private ObjectBigList<DoubleBigList> matLnkProbabilities;
  private Double entropy;
  private long fees;
  private IntraFees intraFees;
  private Double efficiency;
  private Double nbCmbnPrfctCj;
  private NbTxos nbTxosPrfctCj;

  public TxProcessorResult(
      int nbCmbn,
      ObjectBigList<IntBigList> matLnkCombinations,
      ObjectBigList<DoubleBigList> matLnkProbabilities,
      Double entropy,
      Set<long[]> dtrmLnksById,
      Txos txos,
      long fees,
      IntraFees intraFees,
      Double efficiency,
      Double nbCmbnPrfctCj,
      NbTxos nbTxosPrfctCj) {
    super(nbCmbn, matLnkCombinations, dtrmLnksById, txos);
    this.matLnkProbabilities = matLnkProbabilities;
    this.entropy = entropy;
    this.fees = fees;
    this.intraFees = intraFees;
    this.efficiency = efficiency;
    this.nbCmbnPrfctCj = nbCmbnPrfctCj;
    this.nbTxosPrfctCj = nbTxosPrfctCj;
  }

  public ObjectBigList<DoubleBigList> getMatLnkProbabilities() {
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

  public Double getNbCmbnPrfctCj() {
    return nbCmbnPrfctCj;
  }

  public NbTxos getNbTxosPrfctCj() {
    return nbTxosPrfctCj;
  }

  public int getNbLinks() {
    return getTxos().getInputs().size() * getTxos().getOutputs().size();
  }

  public Double getDensity() {
    return getEntropy() / (getTxos().getInputs().size() + getTxos().getOutputs().size());
  }

  public int getNbDL() {
    return getDtrmLnksById().size();
  }

  public Double getRatioDL() {
    return ((double) getNbDL()) / (double) getNbLinks();
  }

  public Double getNRatioDL() {
    return 1.0 - getRatioDL();
  }
}
