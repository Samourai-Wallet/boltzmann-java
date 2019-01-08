package com.samourai.efficiencyscore.linker;

import com.samourai.efficiencyscore.beans.Txos;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class TxosLinkerTest {

  private TxosLinker txosLinker = new TxosLinker(0, 300, 12);

  @Test
  public void testUnpackTxos_onePackAlone() {
    Map<String, Long> ins = new LinkedHashMap<>();
    ins.put("PACK_I1", 25029376106L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Map<String, Long> expectedIns = new LinkedHashMap<>();
    expectedIns.put("I0", 5300000000L);
    expectedIns.put("I3", 5000000000L);
    expectedIns.put("I1", 2020000000L);
    expectedIns.put("I2", 3376106L);

    int expectedPackIdx = 0;

    processUnpackTxos(ins, pack, expectedIns, expectedPackIdx);
  }

  @Test
  public void testUnpackTxos_onePackFollowedByOtherInputs() {
    Map<String, Long> ins = new LinkedHashMap<>();
    ins.put("PACK_I1", 25029376106L);
    ins.put("I4", 123L);
    ins.put("I5", 456L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Map<String, Long> expectedIns = new LinkedHashMap<>();
    expectedIns.put("I0", 5300000000L);
    expectedIns.put("I3", 5000000000L);
    expectedIns.put("I1", 2020000000L);
    expectedIns.put("I2", 3376106L);
    expectedIns.put("I4", 123L);
    expectedIns.put("I5", 456L);

    int expectedPackIdx = 0;

    processUnpackTxos(ins, pack, expectedIns, expectedPackIdx);
  }

  @Test
  public void testUnpackTxos_onePackPreceededByOtherInputs() {
    Map<String, Long> ins = new LinkedHashMap<>();
    ins.put("I4", 123L);
    ins.put("I5", 456L);
    ins.put("PACK_I1", 25029376106L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Map<String, Long> expectedIns = new LinkedHashMap<>();
    expectedIns.put("I4", 123L);
    expectedIns.put("I5", 456L);
    expectedIns.put("I0", 5300000000L);
    expectedIns.put("I3", 5000000000L);
    expectedIns.put("I1", 2020000000L);
    expectedIns.put("I2", 3376106L);

    int expectedPackIdx = 2;

    processUnpackTxos(ins, pack, expectedIns, expectedPackIdx);
  }

  @Test
  public void testUnpackTxos_onePackSurroundedByOtherInputs() {
    Map<String, Long> ins = new LinkedHashMap<>();
    ins.put("I4", 123L);
    ins.put("I5", 456L);
    ins.put("PACK_I1", 25029376106L);
    ins.put("I6", 666L);
    ins.put("I7", 777L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Map<String, Long> expectedIns = new LinkedHashMap<>();
    expectedIns.put("I4", 123L);
    expectedIns.put("I0", 5300000000L);
    expectedIns.put("I3", 5000000000L);
    expectedIns.put("I1", 2020000000L);
    expectedIns.put("I2", 3376106L);
    expectedIns.put("I5", 456L);
    expectedIns.put("I6", 666L);
    expectedIns.put("I7", 777L);

    int expectedPackIdx = 2;

    processUnpackTxos(ins, pack, expectedIns, expectedPackIdx);
  }

  private void processUnpackTxos(
      Map<String, Long> currentTxos,
      Pack pack,
      Map<String, Long> expectedTxos,
      int expectedPackIdx) {
    Map<String, Long> unpackedTxos = new HashMap<>();
    int packIdx = txosLinker.unpackTxos(currentTxos, pack, unpackedTxos);
    Assert.assertEquals(expectedTxos, unpackedTxos);
    Assert.assertEquals(expectedPackIdx, packIdx);
  }

  @Test
  public void testUnpackLinkMatrix_onePackAlone() {
    Txos txos = new Txos();
    txos.getInputs().put("PACK_I1", 25029376106L);

    txos.getOutputs().put("O1", 111111L);
    txos.getOutputs().put("O2", 222222L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Txos expectedTxos = new Txos();
    expectedTxos.getInputs().put("I0", 5300000000L);
    expectedTxos.getInputs().put("I3", 5000000000L);
    expectedTxos.getInputs().put("I1", 2020000000L);
    expectedTxos.getInputs().put("I2", 3376106L);

    expectedTxos.getOutputs().put("O1", 111111L);
    expectedTxos.getOutputs().put("O2", 222222L);

    int[][] matLnk = new int[][] {new int[] {1}, new int[] {11}};

    int[][] expectedMatLnk = new int[][] {new int[] {1, 1, 1, 1}, new int[] {11, 11, 11, 11}};

    unpackLinkMatrix(matLnk, txos, pack, expectedMatLnk, expectedTxos);
  }

  @Test
  public void testUnpackLinkMatrix_onePackFollowedByOtherInputs() {
    Txos txos = new Txos();
    txos.getInputs().put("PACK_I1", 25029376106L);
    txos.getInputs().put("I4", 123L);
    txos.getInputs().put("I5", 456L);

    txos.getOutputs().put("O1", 111111L);
    txos.getOutputs().put("O2", 222222L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Txos expectedTxos = new Txos();
    expectedTxos.getInputs().put("I0", 5300000000L);
    expectedTxos.getInputs().put("I3", 5000000000L);
    expectedTxos.getInputs().put("I1", 2020000000L);
    expectedTxos.getInputs().put("I2", 3376106L);
    expectedTxos.getInputs().put("I4", 123L);
    expectedTxos.getInputs().put("I5", 456L);

    expectedTxos.getOutputs().put("O1", 111111L);
    expectedTxos.getOutputs().put("O2", 222222L);

    int[][] matLnk = new int[][] {new int[] {1, 4, 5}, new int[] {11, 44, 55}};

    int[][] expectedMatLnk =
        new int[][] {new int[] {1, 1, 1, 1, 4, 5}, new int[] {11, 11, 11, 11, 44, 55}};

    unpackLinkMatrix(matLnk, txos, pack, expectedMatLnk, expectedTxos);
  }

  @Test
  public void testUnpackLinkMatrix_onePackPreceededByOtherInputs() {
    Txos txos = new Txos();
    txos.getInputs().put("I4", 123L);
    txos.getInputs().put("I5", 456L);
    txos.getInputs().put("PACK_I1", 25029376106L);

    txos.getOutputs().put("O1", 111111L);
    txos.getOutputs().put("O2", 222222L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Txos expectedTxos = new Txos();
    expectedTxos.getInputs().put("I4", 123L);
    expectedTxos.getInputs().put("I5", 456L);
    expectedTxos.getInputs().put("I0", 5300000000L);
    expectedTxos.getInputs().put("I3", 5000000000L);
    expectedTxos.getInputs().put("I1", 2020000000L);
    expectedTxos.getInputs().put("I2", 3376106L);

    expectedTxos.getOutputs().put("O1", 111111L);
    expectedTxos.getOutputs().put("O2", 222222L);

    int[][] matLnk = new int[][] {new int[] {4, 5, 1}, new int[] {44, 55, 11}};

    int[][] expectedMatLnk =
        new int[][] {new int[] {4, 5, 1, 1, 1, 1}, new int[] {44, 55, 11, 11, 11, 11}};

    unpackLinkMatrix(matLnk, txos, pack, expectedMatLnk, expectedTxos);
  }

  @Test
  public void testUnpackLinkMatrix_onePackSurroundedByOtherInputs() {
    Txos txos = new Txos();
    txos.getInputs().put("I4", 123L);
    txos.getInputs().put("I5", 456L);
    txos.getInputs().put("PACK_I1", 25029376106L);
    txos.getInputs().put("I6", 666L);
    txos.getInputs().put("I7", 777L);

    txos.getOutputs().put("O1", 111111L);
    txos.getOutputs().put("O2", 222222L);

    List<AbstractMap.SimpleEntry<String, Long>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("I0", 5300000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I3", 5000000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I1", 2020000000L));
    entries.add(new AbstractMap.SimpleEntry<>("I2", 3376106L));

    Pack pack = new Pack("PACK_I1", PackType.INPUTS, entries, null);

    Txos expectedTxos = new Txos();
    expectedTxos.getInputs().put("I4", 123L);
    expectedTxos.getInputs().put("I5", 456L);
    expectedTxos.getInputs().put("I0", 5300000000L);
    expectedTxos.getInputs().put("I3", 5000000000L);
    expectedTxos.getInputs().put("I1", 2020000000L);
    expectedTxos.getInputs().put("I2", 3376106L);
    expectedTxos.getInputs().put("I6", 666L);
    expectedTxos.getInputs().put("I7", 777L);

    expectedTxos.getOutputs().put("O1", 111111L);
    expectedTxos.getOutputs().put("O2", 222222L);

    int[][] matLnk = new int[][] {new int[] {4, 5, 1, 6, 7}, new int[] {44, 55, 11, 66, 77}};

    int[][] expectedMatLnk =
        new int[][] {
          new int[] {4, 5, 1, 1, 1, 1, 6, 7}, new int[] {44, 55, 11, 11, 11, 11, 66, 77}
        };

    unpackLinkMatrix(matLnk, txos, pack, expectedMatLnk, expectedTxos);
  }

  private void unpackLinkMatrix(
      int[][] matLnk, Txos txos, Pack pack, int[][] expectedMatLnk, Txos expectedTxos) {
    UnpackLinkMatrixResult result = txosLinker.unpackLinkMatrix(matLnk, txos, pack);

    Assert.assertEquals(expectedTxos.getInputs(), result.getTxos().getInputs());
    Assert.assertEquals(expectedTxos.getOutputs(), result.getTxos().getOutputs());
    Assert.assertTrue(Arrays.deepEquals(expectedMatLnk, result.getMatLnk()));
  }
}
