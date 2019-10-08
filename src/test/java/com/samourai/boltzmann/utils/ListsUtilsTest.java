package com.samourai.boltzmann.utils;

import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class ListsUtilsTest {

  @Test
  public void testPowerSet3() {
    Long[] values = new Long[] {1L, 2L, 3L};
    long[][] expected = {{}, {1}, {2}, {1, 2}, {3}, {1, 3}, {2, 3}, {1, 2, 3}};
    ObjectBigList<long[]> actual = ListsUtils.powerSet(values);
    Assert.assertTrue(ListsUtils.deepEquals(expected, actual));
  }

  @Test
  public void testPowerSetLSize() {
    doTestPowerSetSize(10);
    doTestPowerSetSize(20);
  }

  private void doTestPowerSetSize(int LEN) {
    Long[] values = new Long[LEN];
    for (int i = 0; i < LEN; i++) {
      values[i] = (long) i;
    }
    long expectedSize = (long) Math.pow(2, LEN);

    ObjectBigList<long[]> actual = ListsUtils.powerSet(values);
    Assert.assertEquals(expectedSize, actual.size64());
  }

  @Test
  public void testMergeSet() {
    String[][] toMerge;
    String[][] expected;

    toMerge =
        new String[][] {
          new String[] {"A", "B", "C"},
          new String[] {"D", "E"},
          new String[] {"F", "G"}
        };
    expected =
        new String[][] {
          new String[] {"A", "B", "C"},
          new String[] {"D", "E"},
          new String[] {"F", "G"}
        };
    processTest(toMerge, expected);

    toMerge =
        new String[][] {
          new String[] {"A", "B", "C"},
          new String[] {"C", "D", "E"},
          new String[] {"F", "G"}
        };
    expected =
        new String[][] {
          new String[] {"A", "B", "C", "D", "E"},
          new String[] {"F", "G"}
        };
    processTest(toMerge, expected);

    toMerge =
        new String[][] {
          new String[] {"A", "B", "C"},
          new String[] {"C"},
          new String[] {"D", "E"}
        };
    expected =
        new String[][] {
          new String[] {"A", "B", "C"},
          new String[] {"D", "E"}
        };
    processTest(toMerge, expected);
  }

  private void processTest(String[][] toMerge, String[][] expected) {
    List<Set<String>> sets = new LinkedList<Set<String>>();
    for (int i = 0; i < toMerge.length; i++) {
      sets.add(new HashSet<String>(Arrays.asList(toMerge[i])));
    }
    Collection<Set<String>> mergedSets = ListsUtils.mergeSets(sets);

    List<Set<String>> expectedSets = new LinkedList<Set<String>>();
    for (int i = 0; i < expected.length; i++) {
      expectedSets.add(new HashSet<String>(Arrays.asList(expected[i])));
    }
    Assert.assertEquals(expectedSets, mergedSets);
  }
}
