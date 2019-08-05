package com.samourai.boltzmann;

import com.samourai.boltzmann.beans.BoltzmannSettings;
import com.samourai.boltzmann.beans.NoLimitSettings;
import com.samourai.boltzmann.linker.TxosLinkerOptionEnum;

public class Application {

  public static void main(String[] args) throws Exception {
    // parse args
    if (args.length < 1) {
      System.err.println("Usage: <txid> [maxCjIntrafeesRatio]");
      return;
    }
    String txid = args[0];
    float maxCjIntrafeesRatio = 0.005f;
    if (args.length > 1) {
      maxCjIntrafeesRatio = Float.parseFloat(args[1]);
    }
    System.out.println(
        "Running Boltzmann: txid=" + txid + ", maxCjIntrafeesRatio=" + maxCjIntrafeesRatio);

    // run
    BoltzmannSettings settings = new NoLimitSettings();
    new Boltzmann(settings)
        .process(
            txid,
            maxCjIntrafeesRatio,
            TxosLinkerOptionEnum.PRECHECK,
            TxosLinkerOptionEnum.LINKABILITY);
  }
}
