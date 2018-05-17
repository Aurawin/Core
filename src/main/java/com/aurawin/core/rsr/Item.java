package com.aurawin.core.rsr;

import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.def.handlers.*;
import com.aurawin.core.rsr.transport.AutoNumber;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.MethodFactory;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.security.Credentials;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;


public abstract class Item  implements Transport,AuthenticateHandler{
    public volatile EnumSet<ItemCommand> Commands = EnumSet.noneOf(ItemCommand.class);
    public Version Version;
    public Buffers Buffers;
    public Credentials Credentials;
    public boolean Infinite;
    protected TransportConnect connectionData;
    public int Timeout;
    public ItemKind Kind;
    public ItemState State;

    public Instant TTL;
    public InetSocketAddress Address;
    public InetSocketAddress bindAddress;
    public AutoNumber Id;
    protected SocketHandler SocketHandler;

    public EnumSet<ItemError> Errors;

    public Items Owner;
    public MethodFactory Methods;
    private boolean Released;
    public TransportConnect getConnectionData() {
        return connectionData;
    }

    public Persist getPersistant() {
        return (connectionData!=null) ?  connectionData.getPersistent() : null;
    }

    @SuppressWarnings("unchecked")
    public Item(Items aOwner, ItemKind aKind) throws InvocationTargetException,NoSuchMethodException,
            InstantiationException, IllegalAccessException{
        Released=false;
        Id = new AutoNumber();
        Protocol TA = getClass().getAnnotation(Protocol.class);
        Class v = TA.Version();
        Version = (Version) v.getConstructor().newInstance();
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

    public abstract Item newInstance(Items aOwner) throws NoSuchMethodException,InvocationTargetException,InstantiationException, IllegalAccessException;
    public abstract Item newInstance(Items aOwner, SocketChannel aChannel, ItemKind aKind)throws NoSuchMethodException,InvocationTargetException,InstantiationException, IllegalAccessException;
    public abstract void registerSecurityMechanisms();

    protected void setOwner(Items aOwner){
        Owner=aOwner;
    }
    @Override
    public void Release() {
        if (!Released){
            Released=true;
            Credentials.Release();
            SocketHandler.Release();
            if (connectionData!=null) {
                connectionData.Release();
                connectionData=null;
            }
            Buffers.Release();
            Buffers=null;
            Credentials=null;
            SocketHandler=null;
        }
    }

    public void renewTTL(){
        TTL = ( (Infinite==true)|| (TTL==null) ) ? null : Instant.now().plusMillis(Timeout);
        if (getPersistant()!=null)
            getPersistant().renewTTL();
    }

    public long getRemoteIp(){
        return IpHelper.toLong(this.Address.getAddress().getAddress());
    }

    @Override
    public void Setup(){
        SocketHandler.Setup();
        renewTTL();
    }
    @Override
    public void Teardown(){
        Id = null;
        SocketHandler.Teardown();
        Timeout=0;
        TTL=null;
    }
}
