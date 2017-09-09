package com.samourai.efficiencyscore.linker;

public class IntraFees {

    private int feesMaker;
    private int feesTaker;
    private boolean hasFees;

    public IntraFees(int feesMaker, int feesTaker) {
        this.feesMaker = feesMaker;
        this.feesTaker = feesTaker;
        hasFees = (feesMaker>0 || feesTaker>0);
    }

    public int getFeesMaker() {
        return feesMaker;
    }

    public int getFeesTaker() {
        return feesTaker;
    }

    public boolean hasFees() {
        return hasFees;
    }
}
