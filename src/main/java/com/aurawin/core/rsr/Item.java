package com.aurawin.core.rsr;

import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.plugin.Plug;
import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.def.handlers.*;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Factory;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;

import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.EnumSet;


public abstract class Item  implements Transport,AuthenticateHandler{
    public volatile Version Version;
    public volatile Buffers Buffers;
    public boolean Infinite;

    public int Timeout;
    public ItemKind Kind;
    public ItemState State;

    public Instant TTL;

    protected SocketHandler SocketHandler;

    public EnumSet<ItemError> Errors;

    public Items Owner;
    public Factory Methods;

    public Item(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException{
        Protocol TA = getClass().getAnnotation(Protocol.class);
        Class v = TA.Version();
        Version = (Version) v.newInstance();

        Kind = aKind;
        Errors = EnumSet.noneOf(ItemError.class);
        Buffers = new Buffers();
        Timeout = Settings.RSR.Server.Timeout;
        Methods = new Factory();

        if (aOwner!=null){
            Owner = aOwner;
            Infinite = aOwner.Infinite;
            SocketHandler = aOwner.Engine.createSocketHandler(this);

        } else {
            Infinite = Settings.RSR.Finite;
        }
    }

    public abstract Item newInstance(Items aOwner) throws InstantiationException, IllegalAccessException;
    public abstract Item newInstance(Items aOwner, SocketChannel aChannel)throws InstantiationException, IllegalAccessException;

    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }

    public void Release() throws Exception{
        SocketHandler.Release();
        Buffers.Release();
        Buffers=null;
    }
    public void renewTTL(){
        TTL = ( (Infinite==true)|| (TTL==null) ) ? null : Instant.now().plusMillis(Timeout);
    }
    public String getHostName(){
        return Owner.getHostName();
    }
    public int getPort(){
        return Owner.getPort();
    }
    @Override
    public void Setup(){
        Owner.add(this);
        SocketHandler.Setup(Kind == ItemKind.Server);
        TTL=Instant.now().plusMillis(Timeout);
    }
    @Override
    public void Teardown(){
        SocketHandler.Teardown();
        Timeout=0;
        TTL=null;
        Owner.remove(this);
        Owner.qRemoveItems.remove(this);
        Owner.qWriteItems.remove(this);
    }
    public void queueSend(){
        Owner.qWriteItems.add(this);
    }
    public void queueClose(){
        Owner.qRemoveItems.add(this);
    }
}
