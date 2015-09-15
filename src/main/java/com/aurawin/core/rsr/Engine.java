package com.aurawin.core.rsr;


import com.aurawin.core.rsr.def.EngineState;
import static com.aurawin.core.rsr.def.EngineState.*;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class Engine extends Thread {
    public volatile EngineState State = esNone;
    public Boolean Infinite = false;
    public Class<? extends Item> itmClass;
    public volatile int BufferSizeRead;
    public volatile int BufferSizeWrite;
    public Managers Managers;

    public <T extends Item> Engine(Class<T> cItem, boolean aInfinate) throws IOException {
        Infinite=aInfinate;
        itmClass=cItem;
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
