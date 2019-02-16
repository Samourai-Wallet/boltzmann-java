package com.samourai.boltzmann;

import com.google.common.math.DoubleMath;
import com.samourai.boltzmann.beans.BoltzmannResult;
import com.samourai.boltzmann.beans.BoltzmannSettings;
import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.linker.TxosLinkerOptionEnum;
import com.samourai.boltzmann.processor.TxProcessor;
import com.samourai.boltzmann.processor.TxProcessorResult;
import com.samourai.boltzmann.utils.ListsUtils;
import java.util.Arrays;
import java8.util.stream.LongStreams;

public class Boltzmann {
  private BoltzmannSettings settings;
  private TxProcessor txProcessor;

  public Boltzmann() {
    this(new BoltzmannSettings());
  }

  public Boltzmann(BoltzmannSettings settings) {
    this.settings = settings;
    this.txProcessor = new TxProcessor(settings.getMaxDuration(), settings.getMaxTxos());
  }

  public BoltzmannResult process(Txos txos) {
    return process(txos, settings.getMaxCjIntrafeesRatio(), settings.getOptions());
  }

  public BoltzmannResult process(
      Txos txos, float maxCjIntrafeesRatio, TxosLinkerOptionEnum... linkerOptions) {
    long t1 = System.currentTimeMillis();

    long sumInputs = LongStreams.of(ListsUtils.toPrimitiveArray(txos.getInputs().values())).sum();
    long sumOutputs = LongStreams.of(ListsUtils.toPrimitiveArray(txos.getOutputs().values())).sum();
    long fees = sumInputs - sumOutputs;
    System.out.println("fees = " + fees);

    TxProcessorResult txProcessorResult =
        txProcessor.processTx(txos, maxCjIntrafeesRatio, linkerOptions);
    BoltzmannResult result = new BoltzmannResult(txProcessorResult);
    displayResults(result);

    System.out.println("Duration = " + (System.currentTimeMillis() - t1) + "ms");
    return result;
  }

  /**
   * Displays the results for a given transaction
   *
   * @param result {@link TxProcessorResult}
   */
  protected void displayResults(BoltzmannResult result) {
    System.out.println("Inputs = " + result.getTxos().getInputs());
    System.out.println("Outputs = " + result.getTxos().getOutputs());
    System.out.println("Fees = " + result.getFees() + " satoshis");

    if (result.getIntraFees() != null
        && result.getIntraFees().getFeesMaker() > 0
        && result.getIntraFees().getFeesTaker() > 0) {
      System.out.println(
          "Hypothesis: Max intrafees received by a participant = "
              + result.getIntraFees().getFeesMaker()
              + " satoshis");
      System.out.println(
          "Hypothesis: Max intrafees paid by a participant = "
              + result.getIntraFees().getFeesTaker()
              + " satoshis");
    }

    System.out.println("Nb combinations = " + result.getNbCmbn());
    if (result.getEntropy() != null) {
      System.out.println("Tx entropy = " + result.getEntropy() + " bits");
    }

    if (result.getEfficiency() != null) {
      System.out.println(
          "Wallet efficiency = "
              + (result.getEfficiency() * 100)
              + "% ("
              + DoubleMath.log2(result.getEfficiency())
              + " bits)");
    }

    if (result.getMatLnkCombinations() == null) {
      if (result.getNbCmbn() == 0) {
        System.out.println(
            "Skipped processing of this transaction (too many inputs and/or outputs)");
      }
    } else {
      if (result.getMatLnkProbabilities() != null) {
        System.out.println("Linkability Matrix (probabilities) :");
        System.out.println(Arrays.deepToString(result.getMatLnkProbabilities()));
      }
      if (result.getMatLnkCombinations() != null) {
        System.out.println("Linkability Matrix (#combinations with link) :");
        System.out.println(Arrays.deepToString(result.getMatLnkCombinations()));
      }
    }

    System.out.println("Deterministic links :");
    String[][] readableDtrmLnks = result.getDtrmLnks();
    for (String[] dtrmLink : readableDtrmLnks) {
      String out = dtrmLink[0];
      String in = dtrmLink[1];
      System.out.println(in + " & " + out + " are deterministically linked");
    }
  }
}
