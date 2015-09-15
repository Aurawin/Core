package com.aurawin.core.rsr.server;

import com.aurawin.core.rsr.def.server.ItemState;
import com.aurawin.core.rsr.def.server.rsrResult;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.rsr.def.*;
import com.aurawin.core.time.Time;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

public abstract class Item {
    protected Buffers Buffers;
    protected boolean Infinite;
    protected Items Owner;
    protected SocketChannel Channel;
    protected SelectionKey Key;
    protected Date TTL;
    protected int Timeout;

    protected ItemState state;

    protected abstract rsrResult onPeek();
    protected abstract rsrResult onProcess();
    protected abstract rsrResult onDisconnected();
    protected abstract rsrResult onAccepted();
    protected abstract rsrResult onRejected();
    protected abstract rsrResult onError();
    protected abstract rsrResult onFinalize();
    protected abstract rsrResult onInitialize();

    public Item(Items aOwner){
        Infinite = aOwner.Infinite;
        Owner = aOwner;
        Timeout = Settings.RSR.Server.Timeout;

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
        Buffers.Release();
        Buffers=null;

        Channel=null;
        Key=null;
    }
    public void renewTTL(){
        TTL = (Infinite!=true) ? Time.incMilliSeconds(new Date(),Timeout) :  null;
    }
    public int Read(){
        if (Channel.isConnected()==true) {
            Owner.BufferRead.clear();
            try {
                Channel.read(Owner.BufferRead);
            } catch (IOException ioe){
                return -1;
            }
            int iWrite = Buffers.Read.write(Owner.BufferRead);
            Owner.BufferRead.clear();
            return iWrite;
        } else {
            return -1;
        }
    }

}
