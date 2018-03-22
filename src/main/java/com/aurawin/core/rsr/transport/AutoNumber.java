package com.aurawin.core.rsr.transport;

public class AutoNumber {
    private volatile long Id;

    public long getId(){
        return Id;
    }
    public void Reset(){
        Id = 1;
    }

    public long Spin(){
        Id += 1;
        return Id;
    }

}
