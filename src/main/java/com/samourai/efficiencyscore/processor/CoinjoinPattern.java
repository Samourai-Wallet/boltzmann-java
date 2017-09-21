package com.samourai.efficiencyscore.processor;

public class CoinjoinPattern {

    private int nbPtcpts;
    private int cjAmount;

    public CoinjoinPattern(int nbPtcpts, int cjAmount) {
        this.nbPtcpts = nbPtcpts;
        this.cjAmount = cjAmount;
    }

    public int getNbPtcpts() {
        return nbPtcpts;
    }

    public int getCjAmount() {
        return cjAmount;
    }
}
