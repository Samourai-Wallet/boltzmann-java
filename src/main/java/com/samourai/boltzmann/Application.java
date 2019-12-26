package com.samourai.boltzmann;

import com.samourai.boltzmann.beans.BoltzmannSettings;
import com.samourai.boltzmann.beans.NoLimitSettings;
import com.samourai.boltzmann.utils.LogbackUtils;
import org.slf4j.event.Level;

public class Application {

  public static void main(String[] args) throws Exception {
    // TODO dynamic toggle for debug
    LogbackUtils.setLogLevel("com.samourai.boltzmann", Level.DEBUG.toString());

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
    settings.setMaxCjIntrafeesRatio(maxCjIntrafeesRatio);
    new Boltzmann(settings).process(txid);
  }
}
