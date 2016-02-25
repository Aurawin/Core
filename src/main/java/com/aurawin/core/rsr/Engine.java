package com.aurawin.core.rsr;


import com.aurawin.core.plugin.Plugins;
import com.aurawin.core.rsr.def.EngineState;
import static com.aurawin.core.rsr.def.EngineState.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.annotations.StoredAnnotations;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;

public abstract class Engine extends Thread {
    protected Manifest Manifest;
    protected Entities Entities;
    protected Plugins Plugins;

    public volatile EngineState State;
    public volatile String HostName;
    public Boolean Infinite = false;
    public Item itmRoot;

    public volatile int BufferSizeRead;
    public volatile int BufferSizeWrite;
    public Managers Managers;

    public Engine(Item aRootItem, boolean aInfinate, String hostName) throws IOException,NoSuchMethodException {
        HostName = hostName;
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

    public static Manifest createManifest(
            String username,
            String password,
            String host,
            int port,
            int poolsizeMin,
            int poolsizeMax,
            int poolAcrement,
            int statementsMax,
            int timeout,
            String automation,
            String database,
            String dialect,
            String driver

    ){
        StoredAnnotations al = new StoredAnnotations();
        Manifest m = new Manifest(
                username,
                password,
                host,
                port,
                poolsizeMin,
                poolsizeMax,
                poolAcrement,
                statementsMax,
                timeout,
                automation,
                database,
                dialect,
                driver,
                al
        );

        return m;
    }
    public void setManifest(Manifest m){
        Manifest = m;
        Entities = new Entities(m);
        if (Plugins==null){
            Session ssn = Entities.Sessions.openSession();
            try {
                Plugins = new Plugins(ssn);
            } finally{
                ssn.close();
            }
        }

    }

}
