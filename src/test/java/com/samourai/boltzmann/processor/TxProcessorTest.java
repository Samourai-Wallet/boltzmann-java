package com.samourai.boltzmann.processor;

import com.samourai.boltzmann.beans.BoltzmannSettings;
import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.linker.IntraFees;
import com.samourai.boltzmann.linker.TxosLinkerOptionEnum;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class TxProcessorTest {
  private TxProcessor txProcessor =
      new TxProcessor(BoltzmannSettings.MAX_DURATION_DEFAULT, BoltzmannSettings.MAX_TXOS_DEFAULT);

  @Test
  public void testCheckCoinjoinPattern() {
    // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
    Map<String, Long> outs = new HashMap<String, Long>();
    outs.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
    outs.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);
    outs.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
    outs.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);
    processCheckCoinjoinPattern(outs, 2, new CoinjoinPattern(2, 100000));
  }

  private void processCheckCoinjoinPattern(
      Map<String, Long> outs, int maxNbEntities, CoinjoinPattern expected) {
    CoinjoinPattern result = txProcessor.checkCoinjoinPattern(outs, maxNbEntities);

    Assert.assertEquals(expected.getNbPtcpts(), result.getNbPtcpts());
    Assert.assertEquals(expected.getCjAmount(), result.getCjAmount());
  }

  @Test
  public void testComputeCoinjoinIntrafees() {
    // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
    processComputeCoinjoinIntrafees(2, 100000, 0.005f, new IntraFees(500, 500));
  }

  private void processComputeCoinjoinIntrafees(
      int nbPtcpts, int cjAmount, float prctMax, IntraFees expected) {
    IntraFees result = txProcessor.computeCoinjoinIntrafees(nbPtcpts, cjAmount, prctMax);

    Assert.assertEquals(expected.getFeesMaker(), result.getFeesMaker());
    Assert.assertEquals(expected.getFeesTaker(), result.getFeesTaker());
  }

  @Test
  public void entropyTest() {
    // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
    Map<String, Long> ins0 = new HashMap<String, Long>();
    ins0.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000L);
    ins0.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000L);

    Map<String, Long> outs0 = new HashMap<String, Long>();
    outs0.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
    outs0.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);
    outs0.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
    outs0.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);

    Txos txos0 = new Txos(ins0, outs0);

    TxProcessorResult result0 =
        txProcessor.processTx(
            txos0, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(1.5849625007211563f, result0.getEntropy(), 0.01f);
    Assert.assertEquals(0.2641604167f, result0.getDensity(), 0.01f);

    // 742d8e113839946dad9e81c4b5211e959710a55aa499486bf13a3f435b45456c
    Map<String, Long> ins1 = new HashMap<String, Long>();
    ins1.put("bc1q0wsrm6523zkt0vs9dvae7c64d5yuq86x5ec", 2999808L);
    ins1.put("3C6tAAZCTt4bGZ45s4zdhgNpfCFT5Y4b3v", 2999854L);

    Map<String, Long> outs1 = new HashMap<String, Long>();
    outs1.put("1AiruUx2v1De9p1MJgKiUgm75bzUDhGkJT", 427566L);
    outs1.put("1FdoMZmYdJxAeddMPkB57PU39fzFMmqgkg", 427566L);
    outs1.put("3JXHgGknQyFnUKSNnicmcEyXaY2pxY4L3h", 2571867L);
    outs1.put("bc1qj2pyuyx9rercqxvr0sk3mqwlqdrdw7v8dha", 2572242L);

    Txos txos1 = new Txos(ins1, outs1);
    TxProcessorResult result1 =
        txProcessor.processTx(
            txos1, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(2.321928094887362f, result1.getEntropy(), 0.01f);
    Assert.assertEquals(0.386988008158f, result1.getDensity(), 0.01f);
  }

  @Test
  public void efficiencyTest() {
    // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
    Map<String, Long> ins0 = new HashMap<String, Long>();
    ins0.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000L);
    ins0.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000L);

    Map<String, Long> outs0 = new HashMap<String, Long>();
    outs0.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
    outs0.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);
    outs0.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
    outs0.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);

    Txos txos0 = new Txos(ins0, outs0);
    TxProcessorResult result0 =
        txProcessor.processTx(
            txos0, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(0.42857142857142855f, result0.getEfficiency(), 0.01f);

    // 742d8e113839946dad9e81c4b5211e959710a55aa499486bf13a3f435b45456c
    Map<String, Long> ins1 = new HashMap<String, Long>();
    ins1.put("bc1q0wsrm6523zkt0vs9dvae7c64d5yuq86x5ec", 2999808L);
    ins1.put("3C6tAAZCTt4bGZ45s4zdhgNpfCFT5Y4b3v", 2999854L);

    Map<String, Long> outs1 = new HashMap<String, Long>();
    outs1.put("1AiruUx2v1De9p1MJgKiUgm75bzUDhGkJT", 427566L);
    outs1.put("1FdoMZmYdJxAeddMPkB57PU39fzFMmqgkg", 427566L);
    outs1.put("3JXHgGknQyFnUKSNnicmcEyXaY2pxY4L3h", 2571867L);
    outs1.put("bc1qj2pyuyx9rercqxvr0sk3mqwlqdrdw7v8dha", 2572242L);

    Txos txos1 = new Txos(ins1, outs1);
    TxProcessorResult result1 =
        txProcessor.processTx(
            txos1, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(0.7142857142857143f, result1.getEfficiency(), 0.01f);
  }

  @Test
  public void ratioDLTest() {
    // 8e56317360a548e8ef28ec475878ef70d1371bee3526c017ac22ad61ae5740b8
    Map<String, Long> ins0 = new HashMap<String, Long>();
    ins0.put("1FJNUgMPRyBx6ahPmsH6jiYZHDWBPEHfU7", 10000000L);
    ins0.put("1JDHTo412L9RCtuGbYw4MBeL1xn7ZTuzLH", 1380000L);

    Map<String, Long> outs0 = new HashMap<String, Long>();
    outs0.put("18JNSFk8eRZcM8RdqLDSgCiipgnfAYsFef", 9850000L);
    outs0.put("1PA1eHufj8axDWEbYfPtL8HXfA66gTFsFc", 1270000L);
    outs0.put("1JR3x2xNfeFicqJcvzz1gkEhHEewJBb5Zb", 100000L);
    outs0.put("1ALKUqxRb2MeFqomLCqeYwDZK6FvLNnP3H", 100000L);

    Txos txos0 = new Txos(ins0, outs0);

    TxProcessorResult result0 =
        txProcessor.processTx(
            txos0, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(0.75, result0.getNRatioDL(), 0.0001f);

    // 742d8e113839946dad9e81c4b5211e959710a55aa499486bf13a3f435b45456c
    Map<String, Long> ins1 = new HashMap<String, Long>();
    ins1.put("bc1q0wsrm6523zkt0vs9dvae7c64d5yuq86x5ec", 2999808L);
    ins1.put("3C6tAAZCTt4bGZ45s4zdhgNpfCFT5Y4b3v", 2999854L);

    Map<String, Long> outs1 = new HashMap<String, Long>();
    outs1.put("1AiruUx2v1De9p1MJgKiUgm75bzUDhGkJT", 427566L);
    outs1.put("1FdoMZmYdJxAeddMPkB57PU39fzFMmqgkg", 427566L);
    outs1.put("3JXHgGknQyFnUKSNnicmcEyXaY2pxY4L3h", 2571867L);
    outs1.put("bc1qj2pyuyx9rercqxvr0sk3mqwlqdrdw7v8dha", 2572242L);

    Txos txos1 = new Txos(ins1, outs1);
    TxProcessorResult result1 =
        txProcessor.processTx(
            txos1, 0.005f, TxosLinkerOptionEnum.PRECHECK, TxosLinkerOptionEnum.LINKABILITY);

    Assert.assertEquals(1.0, result1.getNRatioDL(), 0.0001f);
  }
}
