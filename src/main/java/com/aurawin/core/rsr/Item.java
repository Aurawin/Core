package com.aurawin.core.rsr;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.def.handlers.*;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.MethodFactory;
import com.aurawin.core.solution.Settings;

import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.EnumSet;


public abstract class Item  implements Transport,AuthenticateHandler{
    public volatile Version Version;
    public volatile Buffers Buffers;
    public volatile Credentials Credentials;
    public boolean Infinite;

    public int Timeout;
    public ItemKind Kind;
    public ItemState State;

    public Instant TTL;

    protected SocketHandler SocketHandler;

    public EnumSet<ItemError> Errors;

    public Items Owner;
    public MethodFactory Methods;



    public Item(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException{
        Protocol TA = getClass().getAnnotation(Protocol.class);
        Class v = TA.Version();
        Version = (Version) v.newInstance();
        Credentials=new Credentials();

        Kind = aKind;
        Errors = EnumSet.noneOf(ItemError.class);
        Buffers = new Buffers();
        Timeout = Settings.RSR.Server.Timeout;
        Methods = new MethodFactory();

        if (aOwner!=null){
            Owner = aOwner;
            Infinite = aOwner.Infinite;
            SocketHandler = aOwner.Engine.createSocketHandler(this);

        } else {
            Infinite = Settings.RSR.Finite;
        }
    }

    public abstract Item newInstance(Items aOwner) throws InstantiationException, IllegalAccessException;
    public abstract Item newInstance(Items aOwner, SocketChannel aChannel, ItemKind aKind)throws InstantiationException, IllegalAccessException;



    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }
    @Override
    public void Release() throws Exception{
        Credentials.Release();
        SocketHandler.Release();
        Buffers.Release();
        Buffers=null;
        Credentials=null;
    }

    public void renewTTL(){
        TTL = ( (Infinite==true)|| (TTL==null) ) ? null : Instant.now().plusMillis(Timeout);
    }
    public long getRemoteIp(){
        return Bytes.toLongByTripple(SocketHandler.Channel.socket().getInetAddress().getAddress());
    }


    @Override
    public void Setup(){
        Owner.add(this);
        SocketHandler.Setup();
        renewTTL();
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
