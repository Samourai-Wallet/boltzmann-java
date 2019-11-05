package com.samourai.boltzmann.utils;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
  private static final Logger log = LoggerFactory.getLogger(Utils.class);
  private static final long BYTE_TO_MB = 1024L * 1024L;
  private static final int LOG_PROGRESS_FREQUENCY = 30; // log progress every 30s

  private static final Map<String, Progress> progressLast = new HashMap<String, Progress>();
  private static final List<Progress> progressResult = new LinkedList<Progress>();

  private static long maxMemUsed = 0;

  public static void logMemory(String msg) {
    if (log.isDebugEnabled()) {
      Runtime runtime = Runtime.getRuntime();
      long freeMemory = runtime.freeMemory() / BYTE_TO_MB;
      log.debug(freeMemory + "M free - " + (msg != null ? msg : ""));

      // update maxMemUsed
      long totalMemory = runtime.totalMemory() / BYTE_TO_MB;
      long used = totalMemory - freeMemory;
      if (used > maxMemUsed) {
        maxMemUsed = used;
      }
    }
  }

  public static void logProgress(String progressId, long current, long target) {
    logProgress(progressId, current, target, "");
  }

  public static void logProgress(String progressId, long current, long target, String msg) {
    Progress progress = progressLast.get(progressId);
    long now = System.currentTimeMillis();
    if (progress != null && (now - progress.getLast()) < (LOG_PROGRESS_FREQUENCY * 1000)) {
      // don't log, too early
      return;
    }

    // update last
    if (progress == null) {
      progress = new Progress(progressId, current, target);
      progressLast.put(progressId, progress);
    } else {
      progress.update(current, target);
    }

    // log
    if (log.isDebugEnabled()) {
      logMemory(progress.getProgress() + " " + msg);
    }
  }

  public static void logProgressDone(String progressId, long target) {
    logProgressDone(progressId, target, "");
  }

  public static void logProgressDone(String progressId, long target, String msg) {
    Progress progress = progressLast.remove(progressId);
    progressResult.add(progress);
    if (log.isDebugEnabled()) {
      String str = progress.done(target, msg);
      logMemory(str);
    }
  }

  public static List<Progress> getProgressResult() {
    return progressResult;
  }

  public static long getMaxMemUsed() {
    return maxMemUsed;
  }
}
