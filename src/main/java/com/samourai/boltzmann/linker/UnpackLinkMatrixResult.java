package com.samourai.boltzmann.linker;

import com.samourai.boltzmann.beans.Txos;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;

public class UnpackLinkMatrixResult {

  private Txos txos;
  private ObjectBigList<IntBigList> matLnk;

  public UnpackLinkMatrixResult(Txos txos, ObjectBigList<IntBigList> matLnk) {
    this.txos = txos;
    this.matLnk = matLnk;
  }

  public Txos getTxos() {
    return txos;
  }

  public ObjectBigList<IntBigList> getMatLnk() {
    return matLnk;
  }
}
