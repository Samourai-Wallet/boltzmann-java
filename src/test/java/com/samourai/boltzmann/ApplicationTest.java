package com.samourai.boltzmann;

import org.junit.Test;

public class ApplicationTest {

  @Test
  public void test() throws Exception {
    String txid = "246d36ee6f7b16652e594afe96ca4dcb71a1c58d65a34aeb076211f0c38c5753";
    // String txid = "0e0337bdf930eba3b082fdfbd30944b18e03f0f810ae531443161f897a4d3db0";
    // String txid = "dcba20fdfe34fe240fa6eacccfb2e58468ba2feafcfff99706145800d09a09a6";
    float maxCjIntrafeesRatio = 0.005f;

    // run
    String[] args = new String[] {txid, Float.toString(maxCjIntrafeesRatio)};
    new Application().main(args);
  }
}
