package com.samourai.boltzmann.aggregator;

import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrayBigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;

public class TxosAggregatorResult {

  private int nbCmbn;
  private ObjectBigList<IntBigList> matLnkCombinations;

  /**
   * @param nbCmbn
   * @param matLnk Matrix of txos linkability: Columns = input txos, Rows = output txos, Cells =
   *     number of combinations for which an input and an output are linked
   */
  public TxosAggregatorResult(int nbCmbn, ObjectBigList<IntBigList> matLnk) {
    this.nbCmbn = nbCmbn;
    this.matLnkCombinations = matLnk;
  }

  public int getNbCmbn() {
    return nbCmbn;
  }

  public ObjectBigList<IntBigList> getMatLnkCombinations() {
    return matLnkCombinations;
  }

  public ObjectBigList<DoubleBigList> computeMatLnkProbabilities() {
    ObjectBigList<DoubleBigList> matLnkProbabilities =
        new ObjectBigArrayBigList<DoubleBigList>(matLnkCombinations.size64());
    if (nbCmbn > 0) {
      for (long i = 0; i < matLnkCombinations.size64(); i++) {
        IntBigList line = matLnkCombinations.get(i);
        DoubleBigList values = new DoubleBigArrayBigList(line.size64());
        for (long j = 0; j < line.size64(); j++) {
          int val = line.getInt(j);
          double value = new Double(val) / nbCmbn;
          values.add(value);
        }
        matLnkProbabilities.add(values);
      }
    } else {
      // TODO ???
      /*for (long i = 0; i < matLnkCombinations.size64(); i++) {
        DoubleBigList line = new DoubleBigArrayBigList(0);
        matLnkProbabilities.add(line);
      }*/
      System.err.println("nbCmbn=0");
    }
    return matLnkProbabilities;
  }

  public double computeEntropy() {
    double entropy = 0;
    if (nbCmbn > 0) {
      entropy = DoubleMath.log2(nbCmbn);
    }
    return entropy;
  }
}
