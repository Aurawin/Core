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
    public Engine(InetSocketAddress sa) throws IOException {
        _state=State.ssCreated;
        _address=sa;
        _cListen = ServerSocketChannel.open();
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
                        // todo log error
                        // todo trip wait timer
                        // todo could be waiting for network interface to appear
                    }
                    break;
                case ssStart:
                    try{
                        _cListen.socket().bind(_address);
                        _cListen.configureBlocking(false);
                        setState(State.ssRun);

                    } catch (IOException e){
                        // log error (only 1 per minute)
                        // try again in a few seconds
                        // network interface maybe swapping
                    }

                    break;
                case ssStop:
                    try {
                        _cListen.close();
                        _cListen=null;
                    } catch  (IOException e){
                        // todo log error
                    }
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
    private synchronized void setState(State state){
        _state=state;
    }
    public synchronized void Start(){

    }
    public synchronized void Stop(){

    }
    public synchronized void CheckForUpdates(){

    }
}
