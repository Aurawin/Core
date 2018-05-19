package com.aurawin.core.rsr;

import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.def.handlers.*;
import com.aurawin.core.rsr.transport.AutoNumber;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.MethodFactory;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.security.Credentials;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

import static com.aurawin.core.rsr.def.ItemState.isConnecting;
import static com.aurawin.core.rsr.def.ItemState.isEstablished;
import static com.aurawin.core.rsr.def.ItemState.isNone;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;


public abstract class Item  implements Transport,AuthenticateHandler{
    public volatile EnumSet<ItemCommand> Commands = EnumSet.noneOf(ItemCommand.class);
    public Version Version;
    public Buffers Buffers;
    public Credentials Credentials;
    public boolean Infinite;
    public int Timeout;
    public SelectionKey keySelect;
    public ItemKind Kind;
    public ItemState State = isNone;
    public SocketChannel Channel;
    public Instant TTL;
    private Instant retryTTL;
    public InetSocketAddress Address;
    public InetSocketAddress bindAddress;
    public AutoNumber Id;
    protected SocketHandler SocketHandler;


    private int Trys;

    protected  Persist Persistent;

    public EnumSet<ItemError> Errors;

    public Items Owner;
    public MethodFactory Methods;
    private boolean Released;


    public Persist getPersistant() {
        return Persistent;
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
        retryTTL = Instant.now();
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

    public void reAllocateChannel() throws IOException {
        if (Channel != null) Channel.close();
        if (keySelect!=null) keySelect.cancel();

        Channel = SocketChannel.open();
        if (bindAddress != null) {
            Channel.bind(bindAddress);
        }
        Channel.configureBlocking(false);
        keySelect=Channel.register(Owner.Keys,OP_CONNECT | OP_READ | OP_WRITE, this);

        Channel.socket().setKeepAlive(false);
        Channel.socket().setReuseAddress(false);
        Channel.socket().setReceiveBufferSize(Settings.RSR.SocketBufferRecvSize);
        Channel.socket().setSendBufferSize(Settings.RSR.SocketBufferSendSize);

    }
    public void resetTrys(){
        Trys = 0;
        if (Persistent!=null) Persistent.resetTrys();
    }

    public void incTrys(){
        Trys += 1;
        if (Persistent!=null) Persistent.reTry();
    }
    public boolean allowedToRetry(){
        return (Trys < Settings.RSR.Items.TransportConnect.MaxTries) ||
                ((Persistent!=null) && (!Persistent.exceededTrys()));

    }
    public void renewRetryTLL(){
        retryTTL = Instant.now().plusMillis(Settings.RSR.Items.TransportConnect.TryInterleave);
    }
    public boolean readyToConnect() {
        if (Persistent != null) {
            return ((State!=isConnecting) && Persistent.readyToTry());
        } else {
            return ((State!=isConnecting) && Instant.now().isAfter(retryTTL));
        }
    }
    public boolean readyForUse(){
        return (State == isEstablished);
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
