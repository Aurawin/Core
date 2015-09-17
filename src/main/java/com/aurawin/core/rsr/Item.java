package com.aurawin.core.rsr;

import com.aurawin.core.rsr.def.ItemState;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.def.Buffers;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.rsr.def.ItemError;
import static com.aurawin.core.rsr.def.ItemError.*;
import com.aurawin.core.time.Time;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.EnumSet;

public abstract class Item {
    public volatile Buffers Buffers;
    protected boolean Infinite;
    protected Items Owner;
    protected SocketChannel Channel;
    protected SelectionKey Key;
    protected Date TTL;
    protected int Timeout;

    protected ItemState State;
    protected EnumSet<ItemError> Errors;

    protected abstract rsrResult onPeek();
    protected abstract rsrResult onProcess();
    protected abstract rsrResult onDisconnected();
    protected abstract rsrResult onAccepted();
    protected abstract rsrResult onRejected();
    protected abstract rsrResult onError();
    protected abstract rsrResult onFinalize();
    protected abstract rsrResult onInitialize();

    public Item(Items aOwner){
        if (aOwner!=null){
            Infinite = aOwner.Infinite;
            Owner = aOwner;
        } else {
            Infinite = Settings.RSR.Finite;
        }
        Errors = EnumSet.noneOf(ItemError.class);
        Buffers = new Buffers();
        Timeout = Settings.RSR.Server.Timeout;
    }
    public abstract Item newInstance(Items aOwner);

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
        TTL = ( (Infinite==true)|| (TTL==null) ) ? null : Time.incMilliSeconds(new Date(),Timeout);
    }
    public void Teardown(){
        if (Channel.isConnected()==true) {
            try{
                Channel.close();
            }catch (IOException ioe){
                // do nothing
            }
        }
        Timeout=0;
        TTL=null; // we don't want any timeout errors
        Key.cancel();
        Owner.qRemoveItems.add(this);
        Owner.qWriteItems.remove(this);
    }
    public int Read(){
        if (Channel.isConnected()==true) {
            Owner.BufferRead.clear();
            try {
                Channel.read(Owner.BufferRead);
            } catch (IOException ioe){
                return 0;
            }
            Owner.BufferRead.flip();
            int iWrite = Buffers.Read.write(Owner.BufferRead);
            Owner.BufferRead.clear();
            return iWrite;
        } else {
            return 0;
        }
    }
    public int Write(){
        int iWritten=0;
        if (Buffers.Write.Size>0) {
            Owner.BufferWrite.clear();
            // initially  1 MB buffer copy
            Buffers.Write.read(Owner.BufferWrite);
            Owner.BufferWrite.flip();
            while (Owner.BufferWrite.hasRemaining()) {
                try {
                    iWritten+=Channel.write(Owner.BufferWrite);
                } catch (IOException ioe){
                    return -1;
                }
                Owner.BufferWrite.compact();
            }
            Buffers.Write.sliceAtPosition();
            if (Buffers.Write.Size==0) Owner.qWriteItems.remove(this);
        } else {
            if (Buffers.Write.Size == 0) Owner.qWriteItems.remove(this);
        }
        return iWritten;
    }


}
