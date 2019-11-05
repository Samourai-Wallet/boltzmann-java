package com.samourai.boltzmann.utils;

public class Progress {
  private String name;
  private long start;
  private long last;
  private long current;
  private long target;
  private double rate;
  private String msg;

  public Progress(String name, long current, long target) {
    this.name = name;
    this.start = System.currentTimeMillis();
    this.last = this.start;
    if (current == 0) {
      current = 1;
    }
    this.current = current;
    if (target == 0) {
      target = 1;
    }
    this.target = target;
    this.rate = 0;
  }

  public long computeElapsed() {
    return last - start;
  }

  public String getProgress() {
    long elapsed = computeElapsed();
    long todo = target - current + 1;
    long donePercent = current * 100 / target;
    String str = "[" + name + "] " + donePercent + "% " + current + "/" + target;
    if (elapsed > 0 || rate > 0) {
      long eta = rate > 0 ? (long) (todo / rate) : 0;
      str +=
          " since "
              + Utils.duration(elapsed / 1000)
              + ", "
              + String.format("%.3f", rate)
              + "/s"
              + ", ETA "
              + Utils.duration(eta);
    }
    str += "...";
    return str;
  }

  public String getResult() {
    long elapsed = computeElapsed();
    String str =
        "["
            + name
            + "] "
            + target
            + "x in "
            + Utils.duration(elapsed)
            + "s ("
            + String.format("%.3f", rate)
            + "/s) "
            + msg;
    return str;
  }

  public void update(long current, long target) {
    long now = System.currentTimeMillis();

    // update progress
    double newRate = ((double) current) / ((now - this.start) / 1000);
    if (rate != 0) {
      // avg
      newRate = (rate + newRate) / 2;
    }

    this.rate = newRate;
    this.current = current;

    this.last = now;
    this.target = target;
  }

  public String done(Long target, String msg) {
    this.last = System.currentTimeMillis();
    this.current = target;
    this.target = target;
    this.msg = msg;

    if (rate == 0) {
      rate = target;
    }

    long elapsed = computeElapsed();
    String str = "[" + name + "] 100% " + target + " done in " + (elapsed / 1000) + "s " + msg;
    return str;
  }

  public String getName() {
    return name;
  }

  public long getTarget() {
    return target;
  }

  public long getLast() {
    return last;
  }

  public double getRate() {
    return rate;
  }

  public String getMsg() {
    return msg;
  }
};
