package com.coq.record.type;

public class Pointers {
    private long start;
    private long len;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "KeyPointer{" +
                "start=" + start +
                ", end=" + len +
                '}';
    }
}