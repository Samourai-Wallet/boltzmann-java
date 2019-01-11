package com.samourai.efficiencyscore.beans;

import com.samourai.efficiencyscore.processor.TxProcessorResult;
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
        r.getEfficiency());
    this.dtrmLnks =
        r.getDtrmLnksById() != null
            ? replaceDtrmLinks(r.getDtrmLnksById(), r.getTxos())
            : new String[][] {};
  }

  private String[][] replaceDtrmLinks(Set<int[]> dtrmLinks, Txos txos) {
    String[][] result = new String[dtrmLinks.size()][2];

    String[] outs = txos.getOutputs().keySet().toArray(new String[] {});
    String[] ins = txos.getInputs().keySet().toArray(new String[] {});

    int i = 0;
    for (int[] dtrmLink : dtrmLinks) {
      String out = outs[dtrmLink[0]];
      String in = ins[dtrmLink[1]];
      result[i] = new String[] {out, in};
      i++;
    }

    return result;
  }

  public String[][] getDtrmLnks() {
    return dtrmLnks;
  }
}
