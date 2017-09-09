package com.samourai.efficiencyscore.processor;

import java.util.Map;

public class FilteredTxos {

    // Txos txo ids to amounts
    private Map<String, Integer> txos;

    // Mapping txo ids to bitcoin addresses
    private Map<String, String> mapIdAddr;

    public FilteredTxos(Map<String, Integer> txos, Map<String, String> mapIdAddr) {
        this.txos = txos;
        this.mapIdAddr = mapIdAddr;
    }

    public Map<String, Integer> getTxos() {
        return txos;
    }

    public Map<String, String> getMapIdAddr() {
        return mapIdAddr;
    }
}
