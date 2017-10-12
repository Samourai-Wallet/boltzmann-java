package com.samourai.efficiencyscore;

import com.google.common.collect.Maps;
import com.samourai.efficiencyscore.client.Client;
import com.samourai.efficiencyscore.linker.*;
import com.samourai.efficiencyscore.beans.Tx;
import com.samourai.efficiencyscore.processor.TxProcessor;
import com.samourai.efficiencyscore.processor.TxProcessorResult;
import com.samourai.efficiencyscore.beans.Txos;
import com.samourai.efficiencyscore.processor.TxProcessorSettings;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class VectorsTest {
    private Client client = new Client();

    @Test
    public void testProcess_basicPaymentTransaction_inputPacking_dcba20fdfe34fe240fa6eacccfb2e58468ba2feafcfff99706145800d09a09a6() {
        Map<String, Long> inputs = new LinkedHashMap<>();
        inputs.put("1QAHGtVG5EXbs1n7BuhyNKr7DGMWQWKHgS", 5300000000L);
        inputs.put("1DV9k4MzaHSHkhNMxHCjKQuXUwZXPUxEwG", 2020000000L);
        inputs.put("1NaNy4UwTGrRcvuufHsVWj1KWGEc3PEk9K", 4975000000L);
        inputs.put("16h7kaoG82k8DFhUgodf4BozYSA5zLNRma", 5000000000L);
        inputs.put("16boodvrd8PVSGPmtB87PD9XfsJzwaMh3T", 5556000000L);
        inputs.put("1KVry787zTL42uZinmpyqW9umC4PbKxPCa", 7150000000L);

        Map<String, Long> outputs = new LinkedHashMap<>();
        outputs.put("1ABoCkCDm7RVwzuapb1TALDNaEDSvP91D8", 1000000L);
        outputs.put("1BPUHdEzaJLz9VBT2d3hivSYEJALHmzrGa", 30000000000L);

        int nbCmbn = 1;
        int[][] matLnkCombinations = new int[][]{{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
        double[][] matLnkProbabilities = new double[][]{{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1}};
        double entropy = 0;
        String[][] expectedReadableDtrmLnks = new String[][]{
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
        TxProcessorResult expected = new TxProcessorResult(nbCmbn, matLnkCombinations, matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
    }

    @Test
    public void testProcess_ambiguousTransaction_8c5feb901f3983b0f28d996f9606d895d75136dbe8d77ed1d6c7340a403a73bf() {
        Map<String, Long> inputs = new LinkedHashMap<>();
        inputs.put("1KHWnqHHx3fQuRwPmwhZGbSYzDbN3SdhoR", 4900000000L);
        inputs.put("15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", 100000000L);

        Map<String, Long> outputs = new LinkedHashMap<>();
        outputs.put("1NKToQ48X5qaMo1ndexWmHKnn6FNNViivq", 4900000000L);
        outputs.put("15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", 100000000L);

        int nbCmbn = 2;
        int[][] matLnkCombinations = new int[][]{{2, 1}, {1, 2}};
        double[][] matLnkProbabilities = new double[][]{{1, 0.5}, {0.5, 1}};
        double entropy = 1;
        String[][] expectedReadableDtrmLnks = new String[][]{
                {"1NKToQ48X5qaMo1ndexWmHKnn6FNNViivq", "1KHWnqHHx3fQuRwPmwhZGbSYzDbN3SdhoR"},
                {"15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX", "15Z5YJaaNSxeynvr6uW6jQZLwq3n1Hu6RX"}
        };
        long fees = 0;
        Double efficiency = 0.6666666666666666;

        TxProcessorSettings settings = new TxProcessorSettings();
        settings.setMaxCjIntrafeesRatio(0);
        IntraFees intraFees = new IntraFees(0, 0);
        TxProcessorResult expected = new TxProcessorResult(nbCmbn, matLnkCombinations, matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
    }

    @Test
    public void testProcess_basicCoinJoinTransactionDarkWallet_8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8() {
        Map<String, Long> inputs = new LinkedHashMap<>();
        inputs.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000L);
        inputs.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000L);

        Map<String, Long> outputs = new LinkedHashMap<>();
        outputs.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
        outputs.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
        outputs.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);
        outputs.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);

        int nbCmbn = 3;
        int[][] matLnkCombinations = new int[][]{{3, 1}, {1, 3}, {2, 2}, {2, 2}};
        double[][] matLnkProbabilities = new double[][]{{1, 0.3333333333333333}, {0.3333333333333333, 1}, {0.6666666666666666, 0.6666666666666666}, {0.6666666666666666, 0.6666666666666666}};
        double entropy = 0;
        String[][] expectedReadableDtrmLnks = new String[][]{
                {"18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", "1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7"},
                {"1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", "1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH"}
        };
        long fees = 60000;
        Double efficiency = 0.42857142857142854;

        TxProcessorSettings settings = new TxProcessorSettings();
        settings.setMaxCjIntrafeesRatio(0);
        IntraFees intraFees = new IntraFees(0, 0);
        TxProcessorResult expected = new TxProcessorResult(nbCmbn, matLnkCombinations, matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

        settings.setMaxCjIntrafeesRatio(0.005f);
        intraFees = new IntraFees(500, 500);
        expected = new TxProcessorResult(nbCmbn, matLnkCombinations,  matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
    }

    @Test
    public void testProcess_coinJoin_7d588d52d1cece7a18d663c977d6143016b5b326404bbf286bc024d5d54fcecb() {
        Map<String, Long> inputs = new LinkedHashMap<>();
        inputs.put("1KMYhyxf3HQ9AS6fEKx4JFeBW9f9fxxwKG", 260994463L);
        inputs.put("1KUMiSa4Asac8arevxQ3Zqy3K4VZGGrgqF", 98615817L);
        inputs.put("1PQWvTNkbcq8g2hLiRHgS4VWVbwnawMt3A", 84911243L);
        inputs.put("1E1PUeh3EmXY4vNf2HkRyXwQFnXHFWgJPP", 79168410L);
        inputs.put("14c8VyzSrR6ibPiQBjEFrrPRYNFfm2Sfhw", 20112774L);

        Map<String, Long> outputs = new LinkedHashMap<>();
        outputs.put("1Dy3ewdSzquMFBGdT4XtJ4GcQgbssSwcxc", 177252160L);
        outputs.put("1JLoZi3H5imXGv365kXhR1HMgkCDJVNu38", 84077613L);
        outputs.put("1BPwP8GXe3qVadzn1ATYCDkUEZ6R9mPxjG", 84077613L);
        outputs.put("1B172idTvupbVHvSwE4zqHm7H9mR7Hy6dy", 84077613L);
        outputs.put("1AY56nwgza7MJ4icMURzMPzLrj3ehLAkgK", 84077613L);
        outputs.put("1K1QZNs9hjVg8HF6AM8UUNADJBLWAR5mhq", 15369204L);
        outputs.put("13vsiRp43UCvpJTNvgh1ma3gGtHiz7ap1u", 14868890L);

        int nbCmbn = 1;
        int[][] matLnkCombinations = new int[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};
        double[][] matLnkProbabilities = new double[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};
        double entropy = 0;
        String[][] expectedReadableDtrmLnks = new String[][]{
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
        TxProcessorResult expected = new TxProcessorResult(nbCmbn, matLnkCombinations, matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);

        /*settings.setMaxCjIntrafeesRatio(0.005f);
        intraFees = new IntraFees(500, 500);
        expected = new TxProcessorResult(nbCmbn, matLnkCombinations,  matLnkProbabilities, entropy, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, settings, expected, expectedReadableDtrmLnks);
        */
    }

    private void processTest(Map<String, Long> inputs, Map<String, Long> outputs, TxProcessorSettings txProcessorSettings, TxProcessorResult expected, String[][] expectedReadableDtrmLnks) {
        long t1 = System.currentTimeMillis();
        long sumInputs = inputs.values().stream().mapToLong(value -> value).sum();
        long sumOutputs = outputs.values().stream().mapToLong(value -> value).sum();
        long fees = sumInputs - sumOutputs;
        System.out.println("fees = "+fees);

        TxProcessor txProcessor = new TxProcessor();

        Txos txos = new Txos(inputs, outputs);
        Tx tx = new Tx(txos);
        TxProcessorResult result = txProcessor.processTx(tx, txProcessorSettings);
        client.displayResults(result);

        System.out.println("Duration = "+ (System.currentTimeMillis() - t1)+"ms");

        Assert.assertEquals(expected.getNbCmbn(), result.getNbCmbn());
        Assert.assertTrue(Arrays.deepEquals(expected.getMatLnkCombinations(), result.getMatLnkCombinations()));
        Assert.assertTrue(Arrays.deepEquals(expected.getMatLnkProbabilities(), result.getMatLnkProbabilities()));
        // System.err.println(Arrays.deepToString(expectedReadableDtrmLnks));
        // System.err.println(Arrays.deepToString(client.replaceDtrmLinks(result.getDtrmLnks(), result.getTxos())));
        Assert.assertTrue(Arrays.deepEquals(expectedReadableDtrmLnks, client.replaceDtrmLinks(result.getDtrmLnks(), result.getTxos())));

        Assert.assertTrue(Maps.difference(expected.getTxos().getInputs(), result.getTxos().getInputs()).areEqual());
        Assert.assertTrue(Maps.difference(expected.getTxos().getOutputs(), result.getTxos().getOutputs()).areEqual());
        Assert.assertEquals(expected.getFees(), result.getFees());
        Assert.assertEquals(expected.getIntraFees().getFeesMaker(), result.getIntraFees().getFeesMaker());
        Assert.assertEquals(expected.getIntraFees().getFeesTaker(), result.getIntraFees().getFeesTaker());
        Assert.assertEquals(expected.getEfficiency(), result.getEfficiency());
    }


}
