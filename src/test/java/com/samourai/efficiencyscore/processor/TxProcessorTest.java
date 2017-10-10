package com.samourai.efficiencyscore.processor;

import com.samourai.efficiencyscore.linker.IntraFees;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TxProcessorTest {
    private TxProcessor txProcessor = new TxProcessor();


    @Test
    public void testCheckCoinjoinPattern() {
        // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
        Map<String, Long> outs = new HashMap<>();
        outs.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
        outs.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);
        outs.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
        outs.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);
        processCheckCoinjoinPattern(outs, 2, new CoinjoinPattern(2, 100000));
    }

    private void processCheckCoinjoinPattern(Map<String, Long> outs, int maxNbEntities, CoinjoinPattern expected) {
        CoinjoinPattern result = txProcessor.checkCoinjoinPattern(outs, maxNbEntities);

        Assert.assertEquals(expected.getNbPtcpts(), result.getNbPtcpts());
        Assert.assertEquals(expected.getCjAmount(), result.getCjAmount());
    }

    @Test
    public void testComputeCoinjoinIntrafees() {
        // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
        processComputeCoinjoinIntrafees(2, 100000, 0.005f, new IntraFees(500, 500));
    }

    private void processComputeCoinjoinIntrafees(int nbPtcpts, int cjAmount, float prctMax, IntraFees expected) {
        IntraFees result = txProcessor.computeCoinjoinIntrafees(nbPtcpts, cjAmount, prctMax);

        Assert.assertEquals(expected.getFeesMaker(), result.getFeesMaker());
        Assert.assertEquals(expected.getFeesTaker(), result.getFeesTaker());
    }


}
