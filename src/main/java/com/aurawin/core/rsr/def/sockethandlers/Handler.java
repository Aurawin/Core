package com.aurawin.core.rsr.def.sockethandlers;

import com.aurawin.core.rsr.Item;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public abstract class Handler implements Methods {
    public Handler(Item owner) {
        Owner = owner;
    }
    protected Item Owner;
    public SocketChannel Channel;
    protected void setChannel(SocketChannel ch){
        Channel=ch;
    }
    public void Release(){
        try {
            if (Channel != null) Channel.close();
        } catch (IOException e){

        } finally{
            Owner=null;
            Channel=null;
        }
    }
}
