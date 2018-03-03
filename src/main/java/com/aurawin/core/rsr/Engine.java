package com.aurawin.core.rsr;


import com.aurawin.core.plugin.Plug;
import com.aurawin.core.plugin.Plugins;
import com.aurawin.core.rsr.commands.Commands;
import com.aurawin.core.rsr.commands.cmdSetBindIPandPort;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.TransportConnect;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.def.handlers.SocketHandler;
import com.aurawin.core.rsr.def.handlers.SocketHandlerPlain;
import com.aurawin.core.rsr.def.handlers.SocketHandlerSecure;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.security.Certificate;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

public abstract class Engine extends Thread  {
    public volatile InetSocketAddress Address;
    protected volatile static long nextId;

    public Plugins Plugins;
    public volatile Security SSL;
    public volatile EngineState State;


    public volatile String Realm;
    public volatile long realmId;

    public volatile String Root;
    public volatile long rootId;
    public volatile String rootDigest;


    public Boolean Infinite = false;
    protected Class<? extends Item>  transportClass;
    protected ItemKind transportKind;
    protected Item transportObject;

    public volatile int BufferSizeRead;
    public volatile int BufferSizeWrite;
    protected Managers Managers;
    public String Stamp;
    protected Commands Commands;

    public Engine(InetSocketAddress address, Class<? extends Item> aTransport, ItemKind aKind, boolean aInfinate) throws
            InvocationTargetException,IOException,NoSuchMethodException,InstantiationException,IllegalAccessException
    {
        nextId=1;
        Address = address;

        Infinite=aInfinate;

        transportClass = aTransport;
        transportObject = aTransport.getConstructor().newInstance();
        transportKind = aKind;

        transportObject.registerSecurityMechanisms();

        BufferSizeRead = Settings.RSR.Server.BufferSizeRead;
        BufferSizeWrite = Settings.RSR.Server.BufferSizeWrite;
        Commands  = new Commands(this);
        Managers = new Managers(this);
        SSL = new Security();
        Plugins = new Plugins();
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

        // Install Mechanisms for Security



    }

    public synchronized SocketHandler createSocketHandler(Item item){
        if (SSL.Enabled) {
            return new SocketHandlerSecure(item);
        } else {
            return new SocketHandlerPlain(item);
        }
    }
    public void Release(){
        Commands.Release();
        Commands=null;
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

    public void adjustIPandPort(String ip, int port){
        Commands.Queue(new cmdSetBindIPandPort(Commands,ip,port));
    }


    public void loadSecurity(long Id){
        Certificate cert= Entities.Lookup(Certificate.class,Id);
        if (cert!=null) {
            try {
                SSL.Load(cert);
            } catch (Exception e){
                if (e!=null) e.getMessage();
            }
        } else{
            SSL.Enabled=false;
        }

    }

    public void installPlugin(Plug plugin){
        Session ssn = Entities.openSession();
        try{
            Plugins.Uninstall(ssn,plugin.getNamespace());
            Plugins.Install(ssn,plugin);
        } finally{
            ssn.close();
        }
    }

    public TransportConnect Connect(InetSocketAddress a) throws Exception{
        return Managers.Connect(a);
    }

}
