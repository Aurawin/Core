package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.ItemState;
import com.aurawin.core.rsr.def.Buffers;
import com.aurawin.core.rsr.def.sockethandlers.Handler;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.rsr.def.ItemError;
import com.aurawin.core.time.Time;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Date;
import java.util.EnumSet;


public abstract class Item  implements Transport {
    public String Protocol;

    public volatile Buffers Buffers;
    public boolean Infinite;

    public int Timeout;
    public ItemKind Kind;
    public ItemState State;
    public Instant TTL;
    protected Handler SocketHandler;
    public EnumSet<ItemError> Errors;

    public Items Owner;

    public Item(Items aOwner, ItemKind aKind){
        com.aurawin.core.rsr.transport.annotations.Transport TA = getClass().getAnnotation(com.aurawin.core.rsr.transport.annotations.Transport.class);
        Protocol = (TA!=null) ? TA.Protocol() : Table.String(Table.Label.Null);
        Kind = aKind;
        Errors = EnumSet.noneOf(ItemError.class);
        Buffers = new Buffers();
        Timeout = Settings.RSR.Server.Timeout;
        if (aOwner!=null){
            Owner = aOwner;
            Infinite = aOwner.Infinite;
            SocketHandler = aOwner.Engine.createSocketHandler(this);
        } else {
            Infinite = Settings.RSR.Finite;
        }


    }
    public abstract Item newInstance(Items aOwner, ItemKind aKind);
    public abstract Item newInstance(Items aOwner, SocketChannel aChannel);

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
        SocketHandler.Setup(Kind == ItemKind.Server);
        Owner.add(this);
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

    public Plugin getPlugin(String Namespace){
        return Owner.Engine.Plugins.getPlugin(Namespace);
    }
}
