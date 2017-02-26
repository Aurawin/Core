package com.aurawin.core.rsr;

import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.def.handlers.AuthenticateHandler;
import com.aurawin.core.rsr.def.handlers.SocketHandler;
import com.aurawin.core.rsr.def.handlers.RequestHandlerState;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Factory;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;

import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.EnumSet;


public abstract class Item  extends AuthenticateHandler implements Transport {
    public volatile Version Version;
    public volatile Buffers Buffers;
    public boolean Infinite;

    public int Timeout;
    public ItemKind Kind;
    public ItemState State;
    public Instant TTL;

    protected SocketHandler SocketHandler;
    protected RequestHandlerState handlerState;

    public EnumSet<ItemError> Errors;

    public Items Owner;
    public Factory Methods;

    public Item(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException{
        Protocol TA = getClass().getAnnotation(Protocol.class);
        Version = TA.Version().newInstance();

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
    public abstract MemoryStream getResponsePayload();
    public abstract MemoryStream getRequestPayload();
    public abstract KeyPairs getResponseHeaders();
    public abstract KeyPairs getRequestHeaders();
    public abstract Plugin getPlugin();
    public abstract KeyItem getPluginMethod();
    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }
    public void  setRequestHandlerState(RequestHandlerState state){
        handlerState=state;
    }
    public RequestHandlerState getRequestHandlerState(){
        return handlerState;
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

    public Plugin getPlugin(String Namespace){
        return Owner.Engine.Plugins.getPlugin(Namespace);
    }
}
