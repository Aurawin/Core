package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;

import static com.aurawin.core.rsr.def.EngineState.*;

import com.aurawin.core.rsr.def.Security;
import com.aurawin.core.solution.Settings;


public class Server extends Engine {
    private InetSocketAddress address;
    private ServerSocketChannel cListen;

    private Security security;


    public Server(InetSocketAddress sa, Item aRootItem, boolean aInfinate, String aHostName) throws IOException,NoSuchMethodException {
        super (aRootItem,aInfinate,aHostName);
        State = esCreated;
        address = sa;
        cListen = ServerSocketChannel.open();
        security = new Security();
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

    public synchronized void LoadPrivateKey(byte[] DerKey){


    }
}
