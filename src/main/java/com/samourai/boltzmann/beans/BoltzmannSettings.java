package com.samourai.boltzmann.beans;

import com.samourai.boltzmann.linker.TxosLinkerOptionEnum;

public class BoltzmannSettings {

  public static final int MAX_DURATION_DEFAULT = 600;
  public static final int MAX_TXOS_DEFAULT = 12;
  public static final int MAX_CJ_INTRAFEES_DEFAULT = 0;
  public static final TxosLinkerOptionEnum[] OPTIONS_DEFAULT =
      new TxosLinkerOptionEnum[] {
        TxosLinkerOptionEnum.PRECHECK,
        TxosLinkerOptionEnum.LINKABILITY,
        TxosLinkerOptionEnum.MERGE_INPUTS
      };

  /** max duration allocated to processing of a single tx (in seconds) */
  private Integer maxDuration;

  /** max number of txos. Txs with more than max_txos inputs or outputs are not processed. */
  private Integer maxTxos;

  /**
   * max intrafees paid by the taker of a coinjoined transaction. Expressed as a percentage of the
   * coinjoined amount.
   */
  private float maxCjIntrafeesRatio;

  /** options to be applied during processing */
  private TxosLinkerOptionEnum[] options;

  public BoltzmannSettings() {
    this.maxDuration = MAX_DURATION_DEFAULT;
    this.maxTxos = MAX_TXOS_DEFAULT;
    this.maxCjIntrafeesRatio = MAX_CJ_INTRAFEES_DEFAULT;
    this.options = OPTIONS_DEFAULT;
  }

  public Integer getMaxDuration() {
    return maxDuration;
  }

  public void setMaxDuration(Integer maxDuration) {
    this.maxDuration = maxDuration;
  }

  public Integer getMaxTxos() {
    return maxTxos;
  }

  public void setMaxTxos(Integer maxTxos) {
    this.maxTxos = maxTxos;
  }

  public float getMaxCjIntrafeesRatio() {
    return maxCjIntrafeesRatio;
  }

  public void setMaxCjIntrafeesRatio(float maxCjIntrafeesRatio) {
    this.maxCjIntrafeesRatio = maxCjIntrafeesRatio;
  }

  public TxosLinkerOptionEnum[] getOptions() {
    return options;
  }

  public void setOptions(TxosLinkerOptionEnum[] options) {
    this.options = options;
  }
}
