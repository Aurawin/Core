package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;

import static com.aurawin.core.rsr.def.EngineState.*;

import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.rsr.def.handlers.AuthenticateHandler;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;


public class Server extends Engine {

    public Server(InetSocketAddress sa, Class<? extends Item>  aTransport, boolean aInfinate, String aHostName)throws
            IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (aTransport,ItemKind.Server,aInfinate,aHostName,sa.getPort());
        State = esCreated;
        Address = sa;
    }
    public Server(Class<? extends Item> aTransport, boolean aInfinate)throws
            IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (aTransport,ItemKind.Server,aInfinate,"",0);
    }
    @Override
    public void run(){
        try {
            while (State != esFinalize) {
                switch (State) {
                    case esRun:
                        try {
                            SocketChannel chRemote = Channel.accept();
                            if (chRemote != null) {
                                Managers.Accept(chRemote);
                            }
                            Managers.cleanupItemThreads();
                        } catch (IOException ioe) {
                            Syslog.Append("Engine", "accept", Table.Format(Table.Exception.RSR.UnableToAcceptSocket, Address.toString()));
                            try {
                                sleep(Settings.RSR.Server.ListenWaitPause);
                            } catch (InterruptedException ie) {

                            }
                        }
                        break;
                    case esConfigure:
                        try {
                            if (Channel!=null) {
                                if (Channel.isOpen())
                                  Channel.close();
                                Managers.Reset();
                            }
                            Channel = ServerSocketChannel.open();
                            Manifest.Verify();
                            State = esStart;
                        } catch (IOException e) {
                            Channel = null;
                        }
                        break;
                    case esStart:
                        try {
                            Channel.bind(Address);
                            Channel.configureBlocking(false);
                            State = esRun;
                        } catch (IOException ioe) {
                            // network interface maybe swapping
                            Syslog.Append("Engine", "bind", Table.Format(Table.Exception.RSR.UnableToBindAddress, Address.toString()));
                            try {
                                sleep(Settings.RSR.Server.BindWaitPause);
                            } catch (InterruptedException ie) {

                            }
                        }
                        break;
                    case esStop:
                        try {
                            Channel.close();
                            Channel = null;
                        } catch (IOException ioe) {
                            // network interface maybe down
                            Syslog.Append("Engine", "close", Table.Format(Table.Exception.RSR.UnableToCloseAcceptSocket, Address.toString()));
                        }
                        break;
                    case esUpgrade:
                        State = esUpgrading;
                        break;
                    case esUpgrading:
                        //for (cls : ns[])
                        //unload_old_class
                        //load_class
                        State = esRun;
                        break;
                    case esException:
                        break;
                }
                try {
                    sleep(Settings.RSR.Server.AcceptYield);
                } catch (InterruptedException irqe) {
                    Syslog.Append("Server", "monitor", "Interrupted");
                }
            }
        } catch (Exception e){
            State = esException;
            Syslog.Append("Server", "run", Table.Format(Table.Exception.RSR.MonitorLoop, e.getMessage()));
        }
    }
    public synchronized void Configure(){
        if (State==esCreated){
            State=esConfigure;
            start();
        }
    }
    public synchronized void Start(){
        if (State==esCreated){
            State=esStart;
            start();
        }
    }
    public synchronized void Stop(){
        if (State!=esFinalize){
            State=esStop;
        }
    }
    public synchronized void CheckForUpdates(){

    }

}
