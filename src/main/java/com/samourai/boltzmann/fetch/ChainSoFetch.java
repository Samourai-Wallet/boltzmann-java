package com.samourai.boltzmann.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samourai.boltzmann.beans.Txos;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChainSoFetch {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public ChainSoFetch() {}

  public Txos fetch(String txid) throws Exception {
    Map<String, Long> ins0 = new HashMap<String, Long>();
    Map<String, Long> outs0 = new HashMap<String, Long>();

    System.setProperty("http.agent", "curl/7.51.0");

    JsonNode obj;

    URL localResource = ChainSoFetch.class.getResource("/chainSoFetch/" + txid + ".json");
    if (localResource != null) {
      // read from local file
      File localJson = new File(localResource.getFile());
      obj = objectMapper.readTree(localJson);
    } else {
      // read fetch
      String url = "https://chain.so/api/v2/get_tx/BTC/" + txid;
      try {
        obj = objectMapper.readTree(new URL(url));
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Transaction not found: " + txid);
      }
    }
    JsonNode dataObj = obj.get("data");

    JsonNode inputs = dataObj.withArray("inputs");
    for (JsonNode input : inputs) {
      String amount = input.get("value").asText();
      long value = (long) (Double.valueOf(amount) * 1e8);
      ins0.put(input.get("address").asText(), value);
    }

    JsonNode outputs = dataObj.withArray("outputs");
    for (JsonNode output : outputs) {
      String amount = output.get("value").asText();
      long value = (long) (Double.valueOf(amount) * 1e8);
      outs0.put(output.get("address").asText(), value);
    }

    Txos txos = new Txos(ins0, outs0);
    return txos;
  }
}
