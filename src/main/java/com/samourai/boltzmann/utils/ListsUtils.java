package com.samourai.boltzmann.utils;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrayBigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigList;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.*;
import java.util.Map.Entry;
import java8.util.function.LongConsumer;
import java8.util.stream.LongStreams;

public class ListsUtils {
  /**
   * Checks if sets from a list of sets share common elements and merge sets when common elements
   * are detected
   *
   * @param sets list of sets
   * @return Returns the list with merged sets.
   */
  public static List<Set<String>> mergeSets(Collection<Set<String>> sets) {
    LinkedList<Set<String>> tmp_sets = new LinkedList<Set<String>>(sets);
    boolean merged = true;
    while (merged) {
      merged = false;
      LinkedList<Set<String>> res = new LinkedList<Set<String>>();
      while (!tmp_sets.isEmpty()) {
        Set<String> current = tmp_sets.poll();
        LinkedList<Set<String>> rest = tmp_sets;
        tmp_sets = new LinkedList<Set<String>>();
        for (Set<String> x : rest) {
          if (Collections.disjoint(current, x)) {
            tmp_sets.add(x);
          } else {
            merged = true;
            current.addAll(x);
          }
        }
        res.add(current);
      }
      tmp_sets = res;
    }
    return tmp_sets;
  }

  public static ObjectBigList<long[]> powerSet(Long[] a) {
    /*int max = 1 << a.length;
    long[][] result = new long[max][];
    Set<Set<Long>> sets = Sets.powerSet(new HashSet<Long>(Arrays.asList(a)));
    int i=0;
    for (Set<Long> set : sets) {
      // Long[] -> long[]
      long[] line = StreamSupport.stream(set).mapToLong(new ToLongFunction<Long>() {
        @Override
        public long applyAsLong(Long aLong) {
          return aLong;
        }
      }).toArray();
      result[i] = line;
      i++;
    }
    return result;*/
    long max = 1L << a.length;
    ObjectBigList<long[]> result = new ObjectBigArrayBigList<long[]>(max);
    for (int i = 0; i < max; ++i) {
      long[] line = new long[Long.bitCount(i)];
      long b = i;
      for (int j = 0, k = 0; j < a.length; ++j, b >>= 1) {
        if ((b & 1) != 0) line[k++] = a[j];
      }
      result.add(line);
    }

    // consistency check
    long expectedSize = (long) Math.pow(2, a.length);
    if (expectedSize != result.size64()) {
      throw new RuntimeException("powerSet size error: " + result.size64() + " vs " + expectedSize);
    }
    return result;
  }

  public static <K, V extends Comparable<? super V>> Map<K, V> sortMap(
      Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
    List<Entry<K, V>> list = new ArrayList<Entry<K, V>>(map.entrySet());
    Collections.sort(list, comparator);

    Map<K, V> result = new LinkedHashMap<K, V>();
    for (Entry<K, V> entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public static <K, V extends Comparable<? super V>>
      Comparator<Map.Entry<K, V>> comparingByValue() {
    return new Comparator<Map.Entry<K, V>>() {
      @Override
      public int compare(Entry<K, V> c1, Entry<K, V> c2) {
        return c1.getValue().compareTo(c2.getValue());
      }
    };
  }

  public static long[] toPrimitiveArray(Collection<Long> items) {
    long[] arr = new long[items.size()];
    Iterator<Long> iter = items.iterator();
    for (int i = 0; iter.hasNext(); i++) {
      arr[i] = iter.next();
    }
    return arr;
  }

  public static ObjectBigList<IntBigList> newIntMatrix(long lines, long cols, int fillValue) {
    ObjectBigList<IntBigList> matCmbn = new ObjectBigArrayBigList<IntBigList>(lines);
    for (long i = 0; i < lines; i++) {
      IntBigList line = ListsUtils.newIntBigList(cols, fillValue);
      matCmbn.add(line);
    }
    return matCmbn;
  }

  public static IntBigList newIntBigList(long size, int fillValue) {
    IntBigList line = new IntBigArrayBigList(size);
    ListsUtils.fill(line, fillValue, size);
    return line;
  }

  public static ObjectBigList<IntBigList> clone(final ObjectBigList<IntBigList> copy) {
    final ObjectBigArrayBigList<IntBigList> c = new ObjectBigArrayBigList(copy.size64());

    LongStreams.range(0, copy.size64())
        .parallel()
        .forEachOrdered(
            new LongConsumer() {
              @Override
              public void accept(long i) {
                IntBigList copyLine = ((IntBigArrayBigList) copy.get(i)).clone();
                c.add(copyLine);
              }
            });
    return c;
  }

  public static <T> void fill(BigList<T> bigList, T value, long size) {
    for (long i = 0; i < size; i++) {
      bigList.add(value);
    }
  }

  public static boolean deepEquals(int[][] value, ObjectBigList<IntBigList> bigList) {
    if (value.length != bigList.size64()) {
      return false;
    }
    for (int i = 0; i < value.length; i++) {
      if (!Arrays.equals(value[i], bigList.get(i).toArray(new int[] {}))) {
        return false;
      }
    }
    return true;
  }

  public static boolean deepEquals(long[][] value, ObjectBigList<long[]> bigList) {
    if (value.length != bigList.size64()) {
      return false;
    }
    for (int i = 0; i < value.length; i++) {
      if (!Arrays.equals(value[i], bigList.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static ObjectBigList<IntBigList> toBigList(int[][] matLnkInt) {
    ObjectBigList<IntBigList> matLnk = new ObjectBigArrayBigList<IntBigList>(matLnkInt.length);
    for (int i = 0; i < matLnkInt.length; i++) {
      matLnk.add(IntBigArrayBigList.wrap(new int[][] {matLnkInt[i]}));
    }
    return matLnk;
  }

  public static ObjectBigList<DoubleBigList> toBigList(double[][] matLnkInt) {
    ObjectBigList<DoubleBigList> matLnk =
        new ObjectBigArrayBigList<DoubleBigList>(matLnkInt.length);
    for (int i = 0; i < matLnkInt.length; i++) {
      matLnk.add(DoubleBigArrayBigList.wrap(new double[][] {matLnkInt[i]}));
    }
    return matLnk;
  }
}
