package com.samourai.efficiencyscore;

import com.google.common.collect.Maps;
import com.samourai.efficiencyscore.client.Client;
import com.samourai.efficiencyscore.linker.*;
import com.samourai.efficiencyscore.beans.Tx;
import com.samourai.efficiencyscore.processor.TxProcessor;
import com.samourai.efficiencyscore.processor.TxProcessorResult;
import com.samourai.efficiencyscore.beans.Txos;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class VectorsTest {
    private Client client = new Client();

    @Test
    public void test() {
        Map<String, Integer> inputs;
        Map<String, Integer> outputs;
        TxosLinkerOptionEnum[] options = new TxosLinkerOptionEnum[]{TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY, TxosLinkerOptionEnum.MERGE_INPUTS};
        int maxDuration = 600; //TODO defaults
        int maxTxos = 12; //TODO defaults

        inputs = new LinkedHashMap<>();
        inputs.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000);
        inputs.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000);

        outputs = new LinkedHashMap<>();
        outputs.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000);
        outputs.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000);
        outputs.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000);
        outputs.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000);

        int nbCmbn = 3;
        int[][] matLnk = new int[][]{{3, 1}, {1, 3}, {2, 2}, {2, 2}};
        String[][] expectedReadableDtrmLnks = new String[][]{
                {"18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", "1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7"},
                {"1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", "1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH"}
        };
        int fees = 60000;
        Double efficiency = 0.42857142857142854;

        float maxCjIntrafeesRatio = 0;
        IntraFees intraFees = new IntraFees(0, 0);
        TxProcessorResult expected = new TxProcessorResult(nbCmbn, matLnk, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, options, maxDuration, maxTxos, maxCjIntrafeesRatio, expected, expectedReadableDtrmLnks);

        maxCjIntrafeesRatio = 0.005f;
        intraFees = new IntraFees(500, 500);
        expected = new TxProcessorResult(nbCmbn, matLnk, null, new Txos(inputs, outputs), fees, intraFees, efficiency);
        processTest(inputs, outputs, options, maxDuration, maxTxos, maxCjIntrafeesRatio, expected, expectedReadableDtrmLnks);

        // TODO more tests
    }

    private void processTest(Map<String, Integer> inputs, Map<String, Integer> outputs, TxosLinkerOptionEnum[] options, int maxDuration, int maxTxos, float maxCjIntrafeesRatio, TxProcessorResult expected, String[][] expectedReadableDtrmLnks) {
        long t1 = System.currentTimeMillis();
        int sumInputs = inputs.values().stream().mapToInt(value -> value).sum();
        int sumOutputs = outputs.values().stream().mapToInt(value -> value).sum();
        int fees = sumInputs - sumOutputs;
        System.out.println("fees = "+fees);

        TxProcessor txProcessor = new TxProcessor();

        Txos txos = new Txos(inputs, outputs);
        Tx tx = new Tx(txos);
        TxProcessorResult result = txProcessor.processTx(tx, new HashSet<>(Arrays.asList(options)), maxDuration, maxTxos, maxCjIntrafeesRatio);
        client.displayResults(result);

        System.out.println("Duration = "+ (System.currentTimeMillis() - t1)+"ms");

        Assert.assertEquals(expected.getNbCmbn(), result.getNbCmbn());
        System.out.println(Arrays.deepToString(expected.getMatLnk()));
        System.out.println(Arrays.deepToString(result.getMatLnk()));
        Assert.assertTrue(Arrays.deepEquals(expected.getMatLnk(), result.getMatLnk()));
        Assert.assertTrue(Arrays.deepEquals(expectedReadableDtrmLnks, client.replaceDtrmLinks(result.getDtrmLnks(), result.getTxos())));

        Assert.assertTrue(Maps.difference(expected.getTxos().getInputs(), result.getTxos().getInputs()).areEqual());
        Assert.assertTrue(Maps.difference(expected.getTxos().getOutputs(), result.getTxos().getOutputs()).areEqual());
        Assert.assertEquals(expected.getFees(), result.getFees());
        Assert.assertEquals(expected.getIntraFees().getFeesMaker(), result.getIntraFees().getFeesMaker());
        Assert.assertEquals(expected.getIntraFees().getFeesTaker(), result.getIntraFees().getFeesTaker());
        Assert.assertEquals(expected.getEfficiency(), result.getEfficiency());
    }


}
