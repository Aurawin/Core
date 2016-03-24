package com.aurawin.core;

import com.aurawin.core.solution.Settings;
import org.hibernate.annotations.Synchronize;

import static java.lang.Thread.sleep;

public class CriticalBlock {
    volatile boolean releaseRequested;
    volatile boolean locked;
    volatile long count;
    Thread currentThread;

    public CriticalBlock(){
        releaseRequested = false;
        locked = false;
        count =0;
        currentThread = null;
    }
    public synchronized void Enter() {
        Thread caller = Thread.currentThread();
        while ( (locked) && (caller != currentThread) && (!releaseRequested) ){
            try {
                caller.sleep(Settings.CriticalLock.WaitingDelayMillis);
            } catch (InterruptedException  ie) {
                // do nothing
                if (!caller.isAlive()){
                    notify();
                    return;
                }
            }
        }
        if (!releaseRequested) {
            locked = true;
            currentThread = caller;
            count++;
        }
    }
    public synchronized void Leave(){
        if (Thread.currentThread()==currentThread){
            count--;
            if (count<=0){
                count=0;
                locked=false;
                notify();
            }
        }
    }

    public synchronized void Release(){
        releaseRequested = true;
        while (count>0) {
            try {
                Thread.currentThread().sleep(Settings.CriticalLock.WaitingDelayMillis);
            } catch (InterruptedException  ie) {
                notify();
            }
        }
    }
}
