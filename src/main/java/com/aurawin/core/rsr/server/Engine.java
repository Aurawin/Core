package com.aurawin.core.rsr.server;


import com.aurawin.core.rsr.def.server.State;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Engine implements Runnable{
    private volatile State _state= State.ssNone;
    private volatile InetSocketAddress _address;
    private volatile ServerSocketChannel _cListen;
    public synchronized void setState(State state){
       _state=state;
    }
    public Engine(InetSocketAddress sa) throws IOException {
        _state=State.ssCreated;
        _address=sa;
        _cListen = ServerSocketChannel.open();
        _cListen.socket().bind(sa);
        _cListen.configureBlocking(false);
    }
    @Override
    public void run(){
        while (true){
            switch (_state) {
                case ssRun :
                    try {
                        SocketChannel chRemote = _cListen.accept();
                        //todo
                        // select client manager
                        // add channel to client Manager queue
                    } catch (IOException e){
                        // todo
                        // trip wait timer
                        // could be waiting for network interface to appear
                    }
                    break;
                case ssStart:
                    setState(State.ssRun);
                    break;
                case ssStop:
                    // do not accept connection requests from channel
                    break;
                case ssUpgrade:
                    // obtain namespace[]
                    // shutdown each namespaced socket
                    setState(State.ssUpgrading);
                    break;
                case ssUpgrading:
                    //for (cls : ns[])
                    //unload_old_class
                    //load_class
                    setState(State.ssRun);
                    break;
            }

        }
    }
}
