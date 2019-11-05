package com.samourai.boltzmann.utils;

public class Progress {
    private String name;
        private long start;
    private long last;
    private long current;
    private long target;
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
        }

    private long computeElapsed() {
    return last - start;
    }

       public String getProgress() {
    long elapsed = computeElapsed();
    long remaining = (elapsed / current) * (target - current);
    long donePercent = current/target*100;

    String str = "["+name+"] "+donePercent+"% "+current+"/"+target;
           if (elapsed > 0 || remaining > 0){
               str += " since " + (elapsed / 1000) + "s, " + (remaining / 1000) + "s ETA";
           }
           str += "...";
    return str;
        }

        public String getResult() {
            long elapsed = computeElapsed();
            String str = "["+name+"] "+target+"x in "+(elapsed/1000)+"s "+msg;
            return str;
        }

        public void update(long current, long target) {
            this.last = System.currentTimeMillis();
            this.current =  current;
            this.target = target;
        }

    public String done(Long target, String msg) {
        this.last = System.currentTimeMillis();
            this.current = target;
            this.target = target;
            this.msg = msg;

        long elapsed = computeElapsed();
        String str = "["+name+"] 100% "+target+" done in "+(elapsed/1000)+"s "+msg;
            return str;
    }

    public long getLast() {
        return last;
    }
};