package com.aurawin.core.rsr.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.solution.Settings;

import static com.aurawin.core.rsr.def.server.State.*;


public class Engine extends Thread {

    protected volatile com.aurawin.core.rsr.def.server.State _state= ssNone;
    private InetSocketAddress _address;
    private ServerSocketChannel _cListen;
    protected Class<? extends Item> _itmClass = null;
    protected Managers _managers = null;
    public <T extends Item> Engine(InetSocketAddress sa, Class<T> cItem) throws IOException {
        _state=ssCreated;
        _address=sa;
        _cListen = ServerSocketChannel.open();
        _itmClass=cItem;
        _managers = new Managers(this);
    }
    @Override
    public void run(){
        while (_state!=ssFinalize) {
            switch (_state) {
                case ssRun:
                    try {
                        SocketChannel chRemote = _cListen.accept();
                        _managers.Accept(chRemote);
                    } catch (IOException ioe) {
                        Syslog.Append("Engine", "accept", Table.Format(Table.Exception.RSR.Server.UnableToAcceptSocket,_address.toString()));
                        try{
                            wait(Settings.RSR.Server.ListenWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case ssStart:
                    try{
                        _cListen.socket().bind(_address);
                        _cListen.configureBlocking(false);
                        setServerState(ssRun);
                    } catch (IOException ioe){
                        // network interface maybe swapping
                        Syslog.Append("Engine", "bind", Table.Format(Table.Exception.RSR.Server.UnableToBindAddress,_address.toString()));
                        try {
                            wait(Settings.RSR.Server.BindWaitPause);
                        } catch (InterruptedException ie){

                        }
                    }
                    break;
                case ssStop:
                    try {
                        _cListen.close();
                        _cListen=null;
                    } catch  (IOException ioe){
                        // network interface maybe down
                        Syslog.Append("Engine", "close", Table.Format(Table.Exception.RSR.Server.UnableToCloseAcceptSocket,_address.toString()));
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
    private synchronized void setServerState(com.aurawin.core.rsr.def.server.State state){
        _state=state;
    }
    public synchronized void Start(){

    }
    public synchronized void Stop(){

    }
    public synchronized void CheckForUpdates(){

    }
}
