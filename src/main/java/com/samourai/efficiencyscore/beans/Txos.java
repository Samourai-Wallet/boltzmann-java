package com.samourai.efficiencyscore.beans;

import java.util.Map;

public class Txos {

    // List of input txos expressed as tuples (id, amount)
    private Map<String, Integer> inputs;

    // List of output txos expressed as tuples (id, amount)
    private Map<String, Integer> outputs;

    public Txos(Map<String, Integer> inputs, Map<String, Integer> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Map<String, Integer> getInputs() {
        return inputs;
    }

    public Map<String, Integer> getOutputs() {
        return outputs;
    }
}
