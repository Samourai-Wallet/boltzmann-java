package com.samourai.boltzmann.utils;

public class Progress {
  private String name;
  private long start;
  private long last;
  private long current;
  private long target;
  private long rate;
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
    long eta = rate * todo;
    long donePercent = current * 100 / target;
    String str = "[" + name + "] " + donePercent + "% " + current + "/" + target;
    if (elapsed > 0 || eta > 0) {
      str += " since " + (elapsed / 1000) + "s, ETA " + eta + "s";
    }
    str += "...";
    return str;
  }

  public String getResult() {
    long elapsed = computeElapsed();
    String str =
        "[" + name + "] " + target + "x in " + (elapsed / 1000) + "s (" + rate + "/s) " + msg;
    return str;
  }

  public void update(long current, long target) {
    long now = System.currentTimeMillis();

    // update rate
    long doneSincePrevious = current - this.current;
    long elapsedSincePrevious = (now - this.last) / 1000;
    this.rate = doneSincePrevious > 0 ? doneSincePrevious / elapsedSincePrevious : 0;

    // update progress
    this.last = now;
    this.current = current;
    this.target = target;
  }

  public String done(Long target, String msg) {
    this.last = System.currentTimeMillis();
    this.current = target;
    this.target = target;
    this.msg = msg;

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

  public long getRate() {
    return rate;
  }

  public String getMsg() {
    return msg;
  }
};
