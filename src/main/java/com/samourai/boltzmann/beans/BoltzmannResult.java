package com.samourai.boltzmann.beans;

import com.samourai.boltzmann.processor.TxProcessorResult;
import java.util.Set;

public class BoltzmannResult extends TxProcessorResult {
  private String[][] dtrmLnks;

  public BoltzmannResult(TxProcessorResult r) {
    super(
        r.getNbCmbn(),
        r.getMatLnkCombinations(),
        r.getMatLnkProbabilities(),
        r.getEntropy(),
        r.getDtrmLnksById(),
        r.getTxos(),
        r.getFees(),
        r.getIntraFees(),
        r.getEfficiency(),
            r.getNbCmbnPrfctCj(),
            r.getNbTxosPrfctCj());
    this.dtrmLnks =
        r.getDtrmLnksById() != null
            ? replaceDtrmLinks(r.getDtrmLnksById(), r.getTxos())
            : new String[][] {};
  }

  private String[][] replaceDtrmLinks(Set<long[]> dtrmLinks, Txos txos) {
    String[][] result = new String[dtrmLinks.size()][2];

    String[] outs = txos.getOutputs().keySet().toArray(new String[] {});
    String[] ins = txos.getInputs().keySet().toArray(new String[] {});

    int i = 0;
    for (long[] dtrmLink : dtrmLinks) {
      String out = outs[(int) dtrmLink[0]]; // TODO !!! cast
      String in = ins[(int) dtrmLink[1]]; // TODO !!! cast
      result[i] = new String[] {out, in};
      i++;
    }

    return result;
  }

  public String[][] getDtrmLnks() {
    return dtrmLnks;
  }
}
