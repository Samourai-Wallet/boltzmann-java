package com.samourai.boltzmann;

import com.samourai.boltzmann.beans.BoltzmannResult;
import com.samourai.boltzmann.beans.BoltzmannSettings;
import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.fetch.OxtFetch;
import com.samourai.boltzmann.linker.TxosLinkerOptionEnum;
import com.samourai.boltzmann.processor.TxProcessor;
import com.samourai.boltzmann.processor.TxProcessorResult;
import com.samourai.boltzmann.utils.ListsUtils;
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

  public BoltzmannResult process(String txid) throws Exception {
    Txos txos = new OxtFetch().fetch(txid);
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

    long duration = (System.currentTimeMillis() - t1) / 1000;
    BoltzmannResult result = new BoltzmannResult(duration, txProcessorResult);
    result.print();
    return result;
  }
}
