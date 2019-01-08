package com.samourai.efficiencyscore.utils;

import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class ListsUtilsTest {

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
    List<Set<String>> sets = new LinkedList<>();
    for (int i = 0; i < toMerge.length; i++) {
      sets.add(new HashSet<>(Arrays.asList(toMerge[i])));
    }
    Collection<Set<String>> mergedSets = ListsUtils.mergeSets(sets);

    List<Set<String>> expectedSets = new LinkedList<>();
    for (int i = 0; i < expected.length; i++) {
      expectedSets.add(new HashSet<>(Arrays.asList(expected[i])));
    }
    Assert.assertEquals(expectedSets, mergedSets);
  }
}
