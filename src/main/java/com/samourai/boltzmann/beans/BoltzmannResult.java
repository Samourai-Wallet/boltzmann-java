package com.samourai.boltzmann.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.math.DoubleMath;
import com.samourai.boltzmann.processor.TxProcessorResult;
import com.samourai.boltzmann.utils.Progress;
import com.samourai.boltzmann.utils.Utils;
import java.util.*;

public class BoltzmannResult extends TxProcessorResult {
  private String[][] dtrmLnks;
  private long duration;

  public BoltzmannResult(long duration, TxProcessorResult r) {
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
    this.duration = duration;
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

  public void print() {
    System.out.println("Inputs = " + getTxos().getInputs());
    System.out.println("Outputs = " + getTxos().getOutputs());
    System.out.println("Fees = " + getFees() + " satoshis");

    if (getIntraFees() != null
        && getIntraFees().getFeesMaker() > 0
        && getIntraFees().getFeesTaker() > 0) {
      System.out.println(
          "Hypothesis: Max intrafees received by a participant = "
              + getIntraFees().getFeesMaker()
              + " satoshis");
      System.out.println(
          "Hypothesis: Max intrafees paid by a participant = "
              + getIntraFees().getFeesTaker()
              + " satoshis");
    }

    if (getNbCmbnPrfctCj() != null) {
      System.out.println(
          "Perfect coinjoin = "
              + getNbCmbnPrfctCj()
              + " combinations (for "
              + getNbTxosPrfctCj().getNbIns()
              + "x"
              + getNbTxosPrfctCj().getNbOuts()
              + ")");
    }
    System.out.println("Nb combinations = " + getNbCmbn());
    if (getEntropy() != null) {
      System.out.println("Tx entropy = " + getEntropy() + " bits");
      System.out.println("Entropy denstity = " + getDensity());
    }

    if (getEfficiency() != null) {
      System.out.println(
          "Wallet efficiency = "
              + (getEfficiency() * 100)
              + "% ("
              + DoubleMath.log2(getEfficiency())
              + " bits)");
    }

    if (getMatLnkCombinations() == null) {
      if (getNbCmbn() == 0) {
        System.out.println(
            "Skipped processing of this transaction (too many inputs and/or outputs)");
      }
    } else {
      if (getMatLnkProbabilities() != null) {
        System.out.println("Linkability Matrix (probabilities):");
        System.out.println(getMatLnkProbabilities());
      }
      if (getMatLnkCombinations() != null) {
        System.out.println("Linkability Matrix (#combinations with link):");
        System.out.println(getMatLnkCombinations());
      }
    }

    if (getDtrmLnks().length > 0) {
      System.out.println("Deterministic links:");
      String[][] readableDtrmLnks = getDtrmLnks();
      for (String[] dtrmLink : readableDtrmLnks) {
        String out = dtrmLink[0];
        String in = dtrmLink[1];
        System.out.println(in + " & " + out + " are deterministically linked");
      }
    } else {
      System.out.println("Deterministic links: none");
    }

    System.out.println("Benchmarks:");
    List<Object[]> benchmarks = new ArrayList<Object[]>();

    benchmarks.add(new Object[] {"duration", duration});
    System.out.println("Duration = " + Utils.duration(duration));

    long maxMem = Utils.getMaxMemUsed();
    benchmarks.add(new Object[] {"maxMem", maxMem});
    System.out.println("Max mem used: " + maxMem + "M");

    for (Progress progress : Utils.getProgressResult()) {
      System.out.println(progress.getResult());
      Object[] result =
          new Object[] {
            progress.getName(),
            progress.getTarget(),
            progress.computeElapsed() / 1000,
            progress.getRate(),
            progress.getMsg()
          };
      benchmarks.add(result);
    }

    try {
      Map export = new LinkedHashMap();
      export.put("ins", getTxos().getInputs());
      export.put("outs", getTxos().getOutputs());
      export.put("nbCmbn", getNbCmbn());
      export.put("mat", getMatLnkCombinations().toString());
      export.put("benchmarks", benchmarks);

      String exportStr = new ObjectMapper().writeValueAsString(export);
      System.out.println("Export: " + exportStr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
