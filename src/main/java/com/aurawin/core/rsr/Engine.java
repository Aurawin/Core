package com.aurawin.core.rsr;


import com.aurawin.core.rsr.def.EngineState;
import static com.aurawin.core.rsr.def.EngineState.*;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;

public abstract class Engine extends Thread {
    public volatile EngineState State;
    public Boolean Infinite = false;
    public Item itmRoot;

    public volatile int BufferSizeRead;
    public volatile int BufferSizeWrite;
    public Managers Managers;

    public Engine(Item aRootItem, boolean aInfinate) throws IOException,NoSuchMethodException {
        Infinite=aInfinate;
        itmRoot=aRootItem;
        BufferSizeRead = Settings.RSR.Server.BufferSizeRead;
        BufferSizeWrite = Settings.RSR.Server.BufferSizeWrite;
        Managers = new Managers(this);
    }
    public synchronized void setReadBufferSize(int size){
        BufferSizeRead = size;
        Managers.adjustReadBufferSize();
    }
    public synchronized void setWriteBufferSize(int size){
        BufferSizeWrite = size;
        Managers.adjustWriteBufferSize();
    }
    public int getReadBufferSize(){
        return BufferSizeRead;
    }
    public int getWriteBufferSize(){
        return BufferSizeWrite;
    }
    public synchronized void setState(EngineState aState) {
        State = aState;
    }
}
