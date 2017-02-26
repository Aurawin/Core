package com.aurawin.core.rsr;


import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.plugin.Plugins;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.Security;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.rsr.def.handlers.AuthenticateHandler;
import com.aurawin.core.rsr.def.handlers.SocketHandler;
import com.aurawin.core.rsr.def.handlers.SocketHandlerPlain;
import com.aurawin.core.rsr.def.handlers.SocketHandlerSecure;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;

import java.io.IOException;

public abstract class Engine extends Thread {
    public volatile static long nextId;
    protected Manifest Manifest;
    protected Entities Entities;
    protected Plugins Plugins;
    public volatile Security Security;
    public volatile EngineState State;
    public volatile String HostName;
    public volatile int Port;
    public Boolean Infinite = false;
    protected Class<? extends Item>  transportClass;
    protected ItemKind transportKind;
    protected Item transportObject;

    public volatile int BufferSizeRead;
    public volatile int BufferSizeWrite;
    public Managers Managers;
    public String Stamp;


    public Engine(Class<? extends Item> aTransport, ItemKind aKind, boolean aInfinate, String hostName, int port) throws
            IOException,NoSuchMethodException,InstantiationException,IllegalAccessException
    {
        nextId=1;
        HostName = hostName;
        Port = port;
        Infinite=aInfinate;

        transportClass = aTransport;
        transportObject = aTransport.newInstance();
        transportKind = aKind;

        BufferSizeRead = Settings.RSR.Server.BufferSizeRead;
        BufferSizeWrite = Settings.RSR.Server.BufferSizeWrite;
        Managers = new Managers(this);
        Security = new Security();
        setName("Engine Thread "+nextId);
        nextId++;
        Stamp = (
            System.getProperty(Settings.Properties.Title)+" Version "+
            System.getProperty(Settings.Properties.Version.Major)+"."+
            System.getProperty(Settings.Properties.Version.Middle)+"."+
            System.getProperty(Settings.Properties.Version.Minor)+" for Java ("+
            System.getProperty(Settings.Properties.Java.Version)+") on "+
            System.getProperty(Settings.Properties.OS.Name)+" "+
            System.getProperty(Settings.Properties.OS.Architecture)+" ("+
            System.getProperty(Settings.Properties.OS.Version)+") "+
            System.getProperty(Settings.Properties.Edition)+ " Edition"
        );

    }

    public synchronized SocketHandler createSocketHandler(Item item){
        if (Security.Enabled) {
            return new SocketHandlerSecure(item);
        } else {
            return new SocketHandlerPlain(item);
        }
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
            boolean autocommit,
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
        AnnotatedList al = new AnnotatedList();
        Manifest m = new Manifest(
                username,
                password,
                host,
                port,
                autocommit,
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
            Plugins = new Plugins();
        }
    }
    public void loadSecurity(long Id){
        Certificate cert= Entities.Lookup(com.aurawin.core.stored.entities.Certificate.class,Id);
        if (cert!=null) {
            try {
                Security.setCertificate(cert);
            } catch (Exception e){
                if (e!=null) e.getMessage();
            }
        } else{
            Security.Enabled=false;
        }
    }
    public void installPlugin(Plugin plugin){
        Session ssn = Entities.Factory.openSession();
        try{
            Plugins.Uninstall(ssn,plugin.Header.getNamespace());
            Plugins.Install(ssn,plugin);
        } finally{
            ssn.close();
        }
    }


}
