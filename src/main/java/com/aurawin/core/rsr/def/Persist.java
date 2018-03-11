package com.aurawin.core.rsr.def;

import com.aurawin.core.solution.Settings;

import java.time.Instant;

public class Persist {
    protected int Delay;
    protected Instant TTL;
    protected int Try;
    protected TransportConnect TransportConnect;

    public Persist(int delay) {
        TTL = null;
        Delay = delay;
        Try = 0;
    }

    public void setDelay(int delay){
        Delay = delay;
        TTL = Instant.now().plusMillis(Delay);
    }
    public void reTry(){
        Try+=1;
        TTL = Instant.now().plusMillis(Delay);
    }
    public boolean readyToTry(){
        return (Instant.now().isAfter(TTL));
    }
    public boolean exceededTrys(){
        return false;

    }
}
