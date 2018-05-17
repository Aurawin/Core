package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;

import static com.aurawin.core.rsr.def.EngineState.*;

import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;



public class Server extends Engine {
    protected ServerSocketChannel Channel;

    public Server(InetSocketAddress sa, Class<? extends Item>  aTransport, boolean aInfinate, String aHostName)throws
            InvocationTargetException,IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (sa,aTransport,ItemKind.Server,aInfinate);
        State = esCreated;
        Realm = aHostName;

    }
    public Server(InetSocketAddress aAddress, Class<? extends Item> aTransport, boolean aInfinate)throws
            InvocationTargetException,IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (aAddress, aTransport,ItemKind.Server,aInfinate);
    }

    @Override
    public void run(){
        boolean bRun = true;
        try {
            while (bRun) {
                switch (State) {
                    case esRun:
                        try {
                            SocketChannel chRemote = Channel.accept();
                            if (chRemote != null) {
                                chRemote.setOption(StandardSocketOptions.SO_LINGER, 3000);
                                chRemote.socket().setReuseAddress(true);
                                Managers.Accept(chRemote);
                            }
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
                            Channel.socket().setReuseAddress(true);
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
                        bRun=false;
                        try {
                            Managers.Reset();

                            Channel.close();
                            Channel = null;
                        } catch (IOException ioe) {
                            // network interface maybe down
                            Syslog.Append("Server", "close", Table.Format(Table.Exception.RSR.UnableToCloseAcceptSocket, Address.toString()));
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
                }
                try {
                    sleep(Settings.RSR.Server.AcceptYield);
                } catch (InterruptedException irqe) {
                    Syslog.Append("Server", "monitor", "Interrupted");
                }
            }
        } catch (Exception e){
            Stop();
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
        State=esStop;
    }
    public synchronized void CheckForUpdates(){

    }

}
