package com.samourai.efficiencyscore.aggregator;

public class TxosAggregatorResult {

    private int nbCmbn;
    private int[][] matLnk;

    /**
     *
     * @param nbCmbn
     * @param matLnk Matrix of txos linkability: Columns = input txos, Rows = output txos, Cells = number of combinations for which an input and an output are linked
     */
    public TxosAggregatorResult(int nbCmbn, int[][] matLnk) {
        this.nbCmbn = nbCmbn;
        this.matLnk = matLnk;
    }

    public int getNbCmbn() {
        return nbCmbn;
    }

    public int[][] getMatLnk() {
        return matLnk;
    }
}
