package com.samourai.efficiencyscore.linker;

/**
 * Actions to be applied by {@link TxosLinker}
 */
public enum TxosLinkerOptionEnum {
    /**
     * compute the linkability matrix
     */
    LINKABILITY,

    /**
     * precheck existence of deterministic links between inputs and outputs
     */
    PRECHECK,

    /**
     * consider that all fees have been paid by a unique sender and manage fees as an additionnal output
     */
    MERGE_FEES
}
