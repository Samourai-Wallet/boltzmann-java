package com.samourai.efficiencyscore;

import com.google.common.collect.Maps;
import com.samourai.efficiencyscore.beans.Tx;
import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.client.Client;
import com.samourai.efficiencyscore.linker.*;
import com.samourai.efficiencyscore.processor.TxProcessor;
import com.samourai.efficiencyscore.processor.TxProcessorResult;
import com.samourai.efficiencyscore.processor.TxProcessorSettings;
import com.samourai.efficiencyscore.utils.ListsUtils;
import java.util.*;
import java8.util.stream.LongStreams;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class VectorsTest {
  private Client client = new Client();

  @Ignore // TODO results ordering
  @Test
  public void testProcess_dcba20fdfe34fe240fa6eacccfb2e58468ba2feafcfff99706145800d09a09a6() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("1QAHGtVG5EXbs1n7BuhyNKr7DGMWQWKHgS", 5300000000L);
    inputs.put("1DV9k4MzaHSHkhNMxHCjKQuXUwZXPUxEwG", 2020000000L);
    inputs.put("1NaNy4UwTGrRcvuufHsVWj1KWGEc3PEk9K", 4975000000L);
    inputs.put("16h7kaoG82k8DFhUgodf4BozYSA5zLNRma", 5000000000L);
    inputs.put("16boodvrd8PVSGPmtB87PD9XfsJzwaMh3T", 5556000000L);
    inputs.put("1KVry787zTL42uZinmpyqW9umC4PbKxPCa", 7150000000L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", 1000000L);
    outputs.put("1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", 30000000000L);

    int nbCmbn = 1;
    int[][] matLnkCombinations = new int[][] {{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
    double[][] matLnkProbabilities = new double[][] {{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
    double entropy = 0;
    String[][] expectedReadableDtrmLnks =
        new String[][] {
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "16boodvrd8PVSGPmtB87PD9XfsJzwaMh3T"},
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "1QAHGtVG5EXbs1n7BuhyNKr7DGMWQWKHgS"},
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "1KVry787zTL42uZinmpyqW9umC4PbKxPCa"},
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "1DV9k4MzaHSHkhNMxHCjKQuXUwZXPUxEwG"},
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "1NaNy4UwTGrRcvuufHsVWj1KWGEc3PEk9K"},
          {"1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", "16h7kaoG82k8DFhUgodf4BozYSA5zLNRma"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "16boodvrd8PVSGPmtB87PD9XfsJzwaMh3T"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "1QAHGtVG5EXbs1n7BuhyNKr7DGMWQWKHgS"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "1KVry787zTL42uZinmpyqW9umC4PbKxPCa"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "1DV9k4MzaHSHkhNMxHCjKQuXUwZXPUxEwG"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "1NaNy4UwTGrRcvuufHsVWj1KWGEc3PEk9K"},
          {"1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", "16h7kaoG82k8DFhUgodf4BozYSA5zLNRma"}
        };
    long fees = 0;
    Double efficiency = 0.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

    settings.setMaxCjIntrafeesRatio(0.005f);
    intraFees = new IntraFees(0, 0);
    expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_8c5feb901f3983b0f28d996f9606d895d75136dbe8d77ed1d6c7340a403a73bf() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("1KHWnqHHx3fQuRwPmwhZGbSYzDbN3SdhoR", 4900000000L);
    inputs.put("15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", 100000000L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("1NKToQ48X5qaMo1ndexWmHKnn6FNNViivq", 4900000000L);
    outputs.put("15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", 100000000L);

    int nbCmbn = 2;
    int[][] matLnkCombinations = new int[][] {{2, 1}, {1, 2}};
    double[][] matLnkProbabilities = new double[][] {{1, 0.5}, {0.5, 1}};
    double entropy = 1;
    String[][] expectedReadableDtrmLnks =
        new String[][] {
          {"1NKToQ48X5qaMo1ndexWmHKnn6FNNViivq", "1KHWnqHHx3fQuRwPmwhZGbSYzDbN3SdhoR"},
          {"15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", "15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX"}
        };
    long fees = 0;
    Double efficiency = 0.6666666666666666;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

    settings.setMaxCjIntrafeesRatio(0.005f);
    intraFees = new IntraFees(0, 0);
    expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void
      testProcess_coinJoin_8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000L);
    inputs.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
    outputs.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
    outputs.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);
    outputs.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);

    int nbCmbn = 3;
    int[][] matLnkCombinations = new int[][] {{3, 1}, {1, 3}, {2, 2}, {2, 2}};
    double[][] matLnkProbabilities =
        new double[][] {
          {1, 0.3333333333333333},
          {0.3333333333333333, 1},
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666}
        };
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks =
        new String[][] {
          {"18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", "1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7"},
          {"1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", "1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH"}
        };
    long fees = 60000;
    Double efficiency = 0.42857142857142854;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

    settings.setMaxCjIntrafeesRatio(0.005f);
    intraFees = new IntraFees(500, 500);
    expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Ignore // TODO results ordering
  @Test
  public void
      testProcess_coinJoin_7d588d52d1cece7a18d663c977d6143016b5b326404bbf286bc024d5d54fcecb() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG", 260994463L);
    inputs.put("1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF", 98615817L);
    inputs.put("1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A", 84911243L);
    inputs.put("14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw", 20112774L);
    inputs.put("1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP", 79168410L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", 14868890L);
    outputs.put("1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", 84077613L);
    outputs.put("1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", 84077613L);
    outputs.put("1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", 15369204L);
    outputs.put("1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", 177252160L);
    outputs.put("1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", 84077613L);
    outputs.put("1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", 84077613L);

    int nbCmbn = 1;
    int[][] matLnkCombinations =
        new int[][] {
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1}
        };
    double[][] matLnkProbabilities =
        new double[][] {
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1}
        };
    double entropy = 0;
    String[][] expectedReadableDtrmLnks =
        new String[][] {
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
          {"13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", "1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF"},
          {"13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
          {"13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", "14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw"},
          {"13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", "1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP"},
          {"13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", "1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A"},
        };
    long fees = 2001;
    Double efficiency = 0.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

    settings.setMaxCjIntrafeesRatio(0.005f);
    efficiency = 0.00026057666988501715;
    nbCmbn = 95;
    matLnkCombinations =
        new int[][] {
          {95, 9, 25, 11, 11},
          {35, 38, 46, 33, 33},
          {35, 38, 46, 33, 33},
          {35, 38, 46, 33, 33},
          {35, 38, 46, 33, 33},
          {9, 27, 43, 73, 73},
          {11, 73, 21, 27, 27}
        };
    matLnkProbabilities =
        new double[][] {
          {1.0, 0.09473684210526316, 0.2631578947368421, 0.11578947368421053, 0.11578947368421053},
          {0.3684210526315789, 0.4, 0.4842105263157895, 0.3473684210526316, 0.3473684210526316},
          {0.3684210526315789, 0.4, 0.4842105263157895, 0.3473684210526316, 0.3473684210526316},
          {0.3684210526315789, 0.4, 0.4842105263157895, 0.3473684210526316, 0.3473684210526316},
          {0.3684210526315789, 0.4, 0.4842105263157895, 0.3473684210526316, 0.3473684210526316},
          {
            0.09473684210526316,
            0.28421052631578947,
            0.45263157894736844,
            0.7684210526315789,
            0.7684210526315789
          },
          {
            0.11578947368421053,
            0.7684210526315789,
            0.22105263157894736,
            0.28421052631578947,
            0.28421052631578947
          }
        };
    entropy = 6.569855608330948;
    expectedReadableDtrmLnks =
        new String[][] {
          {"1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", "1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG"},
        };
    intraFees = new IntraFees(420388, 1261164);
    expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseA() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 8L);
    outputs.put("B", 2L);
    outputs.put("C", 3L);
    outputs.put("D", 7L);

    int nbCmbn = 3;
    int[][] matLnkCombinations = new int[][] {{2, 2}, {2, 2}, {2, 2}, {2, 2}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666}
        };
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.42857142857142854;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

    settings.setMaxCjIntrafeesRatio(0.005f);
    intraFees = new IntraFees(0, 0);
    expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseB() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 8L);
    outputs.put("B", 2L);
    outputs.put("C", 2L);
    outputs.put("D", 8L);

    int nbCmbn = 5;
    int[][] matLnkCombinations = new int[][] {{3, 3}, {3, 3}, {3, 3}, {3, 3}};
    double[][] matLnkProbabilities =
        new double[][] {{0.6, 0.6}, {0.6, 0.6}, {0.6, 0.6}, {0.6, 0.6}};
    double entropy = 2.321928094887362;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.7142857142857143;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseB2() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 10L);
    outputs.put("C", 2L);
    outputs.put("D", 8L);

    int nbCmbn = 3;
    int[][] matLnkCombinations = new int[][] {{2, 2}, {2, 2}, {2, 2}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666}
        };
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.42857142857142854;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseC() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);

    int nbCmbn = 7;
    int[][] matLnkCombinations = new int[][] {{4, 4}, {4, 4}, {4, 4}, {4, 4}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.5714285714285714, 0.5714285714285714},
          {0.5714285714285714, 0.5714285714285714},
          {0.5714285714285714, 0.5714285714285714},
          {0.5714285714285714, 0.5714285714285714}
        };
    double entropy = 2.807354922057604;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseC2() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 10L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);

    int nbCmbn = 3;
    int[][] matLnkCombinations = new int[][] {{2, 2}, {2, 2}, {2, 2}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666},
          {0.6666666666666666, 0.6666666666666666}
        };
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.42857142857142854;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseD() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 10L);
    inputs.put("b", 10L);
    inputs.put("c", 2L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 8L);
    outputs.put("B", 2L);
    outputs.put("C", 2L);
    outputs.put("D", 8L);
    outputs.put("E", 2L);

    int nbCmbn = 28;
    int[][] matLnkCombinations =
        new int[][] {{16, 16, 7}, {16, 16, 7}, {13, 13, 14}, {13, 13, 14}, {13, 13, 14}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.5714285714285714, 0.5714285714285714, 0.25},
          {0.5714285714285714, 0.5714285714285714, 0.25},
          {0.4642857142857143, 0.4642857142857143, 0.5},
          {0.4642857142857143, 0.4642857142857143, 0.5},
          {0.4642857142857143, 0.4642857142857143, 0.5}
        };
    double entropy = 4.807354922057604;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.20588235294117645;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP2() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);

    int nbCmbn = 3;
    int[][] matLnkCombinations = new int[][] {{2, 2}, {2, 2}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.6666666666666666, 0.6666666666666666}, {0.6666666666666666, 0.6666666666666666}
        };
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP3() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);

    int nbCmbn = 16;
    int[][] matLnkCombinations = new int[][] {{8, 8, 8}, {8, 8, 8}, {8, 8, 8}};
    double[][] matLnkProbabilities =
        new double[][] {{0.5, 0.5, 0.5}, {0.5, 0.5, 0.5}, {0.5, 0.5, 0.5}};
    double entropy = 4.0;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP3WithFees() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 3L);
    outputs.put("C", 2L);

    int nbCmbn = 28;
    int[][] matLnkCombinations = new int[][] {{14, 14, 14}, {13, 13, 13}, {13, 13, 13}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.5, 0.5, 0.5},
          {0.4642857142857143, 0.4642857142857143, 0.4642857142857143},
          {0.4642857142857143, 0.4642857142857143, 0.4642857142857143}
        };
    double entropy = 4.807354922057604;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 5;
    Double efficiency = 1.75;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP3b() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 10L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 10L);

    int nbCmbn = 9;
    int[][] matLnkCombinations = new int[][] {{8, 4, 4}, {4, 5, 5}, {4, 5, 5}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.8888888888888888, 0.4444444444444444, 0.4444444444444444},
          {0.4444444444444444, 0.5555555555555556, 0.5555555555555556},
          {0.4444444444444444, 0.5555555555555556, 0.5555555555555556}
        };
    double entropy = 3.1699250014423126;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 0.5625;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP4() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);

    int nbCmbn = 131;
    int[][] matLnkCombinations =
        new int[][] {{53, 53, 53, 53}, {53, 53, 53, 53}, {53, 53, 53, 53}, {53, 53, 53, 53}};
    double[][] matLnkProbabilities =
        new double[][] {
          {0.40458015267175573, 0.40458015267175573, 0.40458015267175573, 0.40458015267175573},
          {0.40458015267175573, 0.40458015267175573, 0.40458015267175573, 0.40458015267175573},
          {0.40458015267175573, 0.40458015267175573, 0.40458015267175573, 0.40458015267175573},
          {0.40458015267175573, 0.40458015267175573, 0.40458015267175573, 0.40458015267175573}
        };
    double entropy = 7.03342300153745;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP5() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);

    int nbCmbn = 1496;
    int[][] matLnkCombinations =
        new int[][] {
          {512, 512, 512, 512, 512},
          {512, 512, 512, 512, 512},
          {512, 512, 512, 512, 512},
          {512, 512, 512, 512, 512},
          {512, 512, 512, 512, 512}
        };
    double[][] matLnkProbabilities =
        new double[][] {
          {
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128
          },
          {
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128
          },
          {
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128
          },
          {
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128
          },
          {
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128,
            0.3422459893048128
          }
        };
    double entropy = 10.546894459887637;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP6() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);
    inputs.put("f", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);
    outputs.put("F", 5L);

    int nbCmbn = 22482;
    int[][] matLnkCombinations =
        new int[][] {
          {6697, 6697, 6697, 6697, 6697, 6697},
          {6697, 6697, 6697, 6697, 6697, 6697},
          {6697, 6697, 6697, 6697, 6697, 6697},
          {6697, 6697, 6697, 6697, 6697, 6697},
          {6697, 6697, 6697, 6697, 6697, 6697},
          {6697, 6697, 6697, 6697, 6697, 6697}
        };
    double[][] matLnkProbabilities =
        new double[][] {
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          },
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          },
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          },
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          },
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          },
          {
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604,
            0.2978827506449604
          }
        };
    double entropy = 14.456482763050271;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  public void testProcess_testCaseP7() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);
    inputs.put("f", 5L);
    inputs.put("g", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);
    outputs.put("F", 5L);
    outputs.put("G", 5L);

    int nbCmbn = 426833;
    int[][] matLnkCombinations =
        new int[][] {
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925},
          {112925, 112925, 112925, 112925, 112925, 112925, 112925}
        };
    double[][] matLnkProbabilities =
        new double[][] {
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          },
          {
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086,
            0.26456482980463086
          }
        };
    double entropy = 18.703312194872563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  @Ignore // TODO performances
  public void testProcess_testCaseP8() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);
    inputs.put("f", 5L);
    inputs.put("g", 5L);
    inputs.put("h", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);
    outputs.put("F", 5L);
    outputs.put("G", 5L);
    outputs.put("H", 5L);

    int nbCmbn = 9934563;
    int[][] matLnkCombinations =
        new int[][] {
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635},
          {2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635, 2369635}
        };
    double[][] matLnkProbabilities =
        new double[][] {
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          },
          {
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339,
            0.23852433166914339
          }
        };
    double entropy = 23.244025077151523;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  @Ignore // TODO performances
  public void testProcess_testCaseP9() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);
    inputs.put("f", 5L);
    inputs.put("g", 5L);
    inputs.put("h", 5L);
    inputs.put("i", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);
    outputs.put("F", 5L);
    outputs.put("G", 5L);
    outputs.put("H", 5L);
    outputs.put("I", 5L);

    int nbCmbn = 277006192;
    int[][] matLnkCombinations = null;
    double[][] matLnkProbabilities = null;
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  @Test
  @Ignore // TODO performances
  public void testProcess_testCaseP10() {
    Map<String, Long> inputs = new LinkedHashMap<String, Long>();
    inputs.put("a", 5L);
    inputs.put("b", 5L);
    inputs.put("c", 5L);
    inputs.put("d", 5L);
    inputs.put("e", 5L);
    inputs.put("f", 5L);
    inputs.put("g", 5L);
    inputs.put("h", 5L);
    inputs.put("i", 5L);
    inputs.put("j", 5L);

    Map<String, Long> outputs = new LinkedHashMap<String, Long>();
    outputs.put("A", 5L);
    outputs.put("B", 5L);
    outputs.put("C", 5L);
    outputs.put("D", 5L);
    outputs.put("E", 5L);
    outputs.put("F", 5L);
    outputs.put("G", 5L);
    outputs.put("H", 5L);
    outputs.put("I", 5L);
    outputs.put("J", 5L);

    int nbCmbn = 22482;
    int[][] matLnkCombinations = null;
    double[][] matLnkProbabilities = null;
    double entropy = 1.5849625007211563;
    String[][] expectedReadableDtrmLnks = new String[][] {};
    long fees = 0;
    Double efficiency = 1.0;

    TxProcessorSettings settings = new TxProcessorSettings();
    settings.setOptions(
        new TxosLinkerOptionEnum[] {
          TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY
        });
    settings.setMaxCjIntrafeesRatio(0);
    IntraFees intraFees = new IntraFees(0, 0);
    TxProcessorResult expected =
        new TxProcessorResult(
            nbCmbn,
            matLnkCombinations,
            matLnkProbabilities,
            entropy,
            null,
            new Txos(inputs, outputs),
            fees,
            intraFees,
            efficiency);
    processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
  }

  private void processTest(
      Map<String, Long> inputs,
      Map<String, Long> outputs,
      TxProcessorSettings txProcessorSettings,
      TxProcessorResult expected,
      String[][] expectedReadableDtrmLnks) {
    long t1 = System.currentTimeMillis();
    long sumInputs = LongStreams.of(ListsUtils.toPrimitiveArray(inputs.values())).sum();
    long sumOutputs = LongStreams.of(ListsUtils.toPrimitiveArray(outputs.values())).sum();
    long fees = sumInputs - sumOutputs;
    System.out.println("fees = " + fees);

    TxProcessor txProcessor = new TxProcessor();

    Txos txos = new Txos(inputs, outputs);
    Tx tx = new Tx(txos);
    TxProcessorResult result = txProcessor.processTx(tx, txProcessorSettings);
    client.displayResults(result);

    System.out.println("Duration = " + (System.currentTimeMillis() - t1) + "ms");

    Assert.assertEquals(expected.getNbCmbn(), result.getNbCmbn());
    Assert.assertTrue(
        Arrays.deepEquals(expected.getMatLnkCombinations(), result.getMatLnkCombinations()));
    Assert.assertTrue(
        Arrays.deepEquals(expected.getMatLnkProbabilities(), result.getMatLnkProbabilities()));
    // System.err.println(Arrays.deepToString(expectedReadableDtrmLnks));
    // System.err.println(Arrays.deepToString(client.replaceDtrmLinks(result.getDtrmLnks(),
    // result.getTxos())));
    Assert.assertTrue(
        Arrays.deepEquals(
            expectedReadableDtrmLnks,
            client.replaceDtrmLinks(result.getDtrmLnks(), result.getTxos())));

    Assert.assertTrue(
        Maps.difference(expected.getTxos().getInputs(), result.getTxos().getInputs()).areEqual());
    Assert.assertTrue(
        Maps.difference(expected.getTxos().getOutputs(), result.getTxos().getOutputs()).areEqual());
    Assert.assertEquals(expected.getFees(), result.getFees());
    Assert.assertEquals(
        expected.getIntraFees().getFeesMaker(), result.getIntraFees().getFeesMaker());
    Assert.assertEquals(
        expected.getIntraFees().getFeesTaker(), result.getIntraFees().getFeesTaker());
    Assert.assertEquals(expected.getEfficiency(), result.getEfficiency());
    Assert.assertEquals(expected.getEntropy(), result.getEntropy());
  }
}
