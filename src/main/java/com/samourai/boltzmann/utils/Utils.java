package com.samourai.boltzmann.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
  private static final Logger log = LoggerFactory.getLogger(Utils.class);
  private static final long BYTE_TO_MB = 1024L * 1024L;

  public static void logMemory() {
    logMemory(null);
  }

  public static void logMemory(String msg) {
    if (log.isDebugEnabled()) {
      Runtime runtime = Runtime.getRuntime();

      long totalMemory = runtime.totalMemory() / BYTE_TO_MB;
      long freeMemory = runtime.freeMemory() / BYTE_TO_MB;
      long memory = totalMemory - freeMemory;
      long percent = 100 * memory / totalMemory;
      log.debug(
          "(" + percent + "% mem used, " + freeMemory + "MB free) " + (msg != null ? msg : ""));
    }
  }
}
