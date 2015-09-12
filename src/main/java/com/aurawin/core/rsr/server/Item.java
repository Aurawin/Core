package com.aurawin.core.rsr.server;

import java.nio.channels.SocketChannel;

abstract class Item extends AItem{
    private Items Owner;
    private SocketChannel Channel;
    public Item(Items aOwner){
        Owner=aOwner;
    }
    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }
    protected void setChannel(SocketChannel ch){
        Channel=ch;
    }
//    protected int onDisconnected(){
//
//    }
//    protected int onAccepted(){
//
//    }
//    int onError(){
//
//    }
//    int onDataReceived(){
//
//    }

}
