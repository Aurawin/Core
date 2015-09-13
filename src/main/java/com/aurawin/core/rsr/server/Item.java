package com.aurawin.core.rsr.server;

import com.aurawin.core.rsr.def.server.ItemState;
import com.aurawin.core.rsr.def.server.rsrResult;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

public abstract class Item {
    protected Items Owner;
    protected SocketChannel Channel;
    protected SelectionKey Key;
    protected Date TTL;

    protected ItemState state;

    protected abstract rsrResult onPeek();
    protected abstract rsrResult onProcess();
    protected abstract rsrResult onDisconnected();
    protected abstract rsrResult onAccepted();
    protected abstract rsrResult onRejected();
    protected abstract rsrResult onError();

    public Item(Items aOwner){
        Owner = aOwner;
    }
    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }
    protected void setChannel(SocketChannel ch){
        Channel=ch;
    }
    public void Release() throws Exception{
        if (Key!=null) Key.cancel();
        if (Channel!=null) Channel.close();

        Channel=null;
        Key=null;
    }

}
