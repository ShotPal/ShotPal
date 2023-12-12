package com.cs407.shotpal;

public class shotClass {

    private long shotTime;
    private long splitTime;

    public shotClass(long shotTime, long splitTime) {
        this.shotTime = shotTime;
        this.splitTime = splitTime;
    }

    public long getShotTime() {
        return shotTime;
    }

    public long getSplitTime() {
        return splitTime;
    }
}
