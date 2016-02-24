package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.EngineState;
import static com.aurawin.core.rsr.def.EngineState.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.annotations.StoredAnnotations;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.UniqueId;


public class Server extends Engine {
    private InetSocketAddress address;
    private ServerSocketChannel cListen;

    public Server(InetSocketAddress sa, Item aRootItem, boolean aInfinate, String aHostName) throws IOException,NoSuchMethodException {
        super (aRootItem,aInfinate,aHostName);
        State = esCreated;
        address = sa;
        cListen = ServerSocketChannel.open();
    }
    @Override
    public void run(){
        while (State !=esFinalize) {
            switch (State) {
                case esRun:
                    try {
                        SocketChannel chRemote = cListen.accept();
                        if (chRemote!=null)
                            Managers.Accept(chRemote);
                    } catch (IOException ioe) {
                        Syslog.Append("Engine", "accept", Table.Format(Table.Exception.RSR.UnableToAcceptSocket, address.toString()));
                        try{
                            sleep(Settings.RSR.Server.ListenWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case esStart:
                    try{
                        cListen.socket().bind(address);
                        cListen.configureBlocking(false);
                        State=esRun;
                    } catch (IOException ioe){
                        // network interface maybe swapping
                        Syslog.Append("Engine", "bind", Table.Format(Table.Exception.RSR.UnableToBindAddress, address.toString()));
                        try {
                            sleep(Settings.RSR.Server.BindWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case esStop:
                    try {
                        cListen.close();
                        cListen =null;
                    } catch  (IOException ioe){
                        // network interface maybe down
                        Syslog.Append("Engine", "close", Table.Format(Table.Exception.RSR.UnableToCloseAcceptSocket, address.toString()));
                    }
                    break;
                case esUpgrade:
                    State=esUpgrading;
                    break;
                case esUpgrading:
                    //for (cls : ns[])
                    //unload_old_class
                    //load_class
                    State=esRun;
                    break;
            }
            try {
              sleep(Settings.RSR.Server.AcceptYield);
            } catch (InterruptedException irqe) {
                // release
            }
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
