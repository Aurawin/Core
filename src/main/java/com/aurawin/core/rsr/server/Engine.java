package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.server.ServerState;
import static com.aurawin.core.rsr.def.server.ServerState.*;
import com.aurawin.core.solution.Settings;



public class Engine extends Thread {
    protected volatile ServerState state = ssNone;
    private InetSocketAddress address;
    private ServerSocketChannel cListen;
    protected Class<? extends Item> itmclass = null;
    protected Managers managers = null;
    public <T extends Item> Engine(InetSocketAddress sa, Class<T> cItem) throws IOException {
        state = ssCreated;
        address = sa;
        cListen = ServerSocketChannel.open();
        itmclass = cItem;
        managers = new Managers(this);
    }
    @Override
    public void run(){
        while (state !=ssFinalize) {
            switch (state) {
                case ssRun:
                    try {
                        SocketChannel chRemote = cListen.accept();
                        managers.Accept(chRemote);
                    } catch (IOException ioe) {
                        Syslog.Append("Engine", "accept", Table.Format(Table.Exception.RSR.Server.UnableToAcceptSocket, address.toString()));
                        try{
                            wait(Settings.RSR.Server.ListenWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case ssStart:
                    try{
                        cListen.socket().bind(address);
                        cListen.configureBlocking(false);
                        setServerState(ssRun);
                    } catch (IOException ioe){
                        // network interface maybe swapping
                        Syslog.Append("Engine", "bind", Table.Format(Table.Exception.RSR.Server.UnableToBindAddress, address.toString()));
                        try {
                            wait(Settings.RSR.Server.BindWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case ssStop:
                    try {
                        cListen.close();
                        cListen =null;
                    } catch  (IOException ioe){
                        // network interface maybe down
                        Syslog.Append("Engine", "close", Table.Format(Table.Exception.RSR.Server.UnableToCloseAcceptSocket, address.toString()));
                    }
                    break;
                case ssUpgrade:
                    // obtain namespace[]
                    // shutdown each namespaced socket
                    setServerState(ssUpgrading);
                    break;
                case ssUpgrading:
                    //for (cls : ns[])
                    //unload_old_class
                    //load_class
                    setServerState(ssRun);
                    break;
            }
            try {
              wait(Settings.RSR.Server.AcceptYield);
            } catch (InterruptedException irqe) {
                // release
            }
        }
    }
    private synchronized void setServerState(ServerState aState) {
        state = aState;
    }
    public synchronized void Start(){

    }
    public synchronized void Stop(){

    }
    public synchronized void CheckForUpdates(){

    }
}
