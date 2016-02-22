package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.ItemState;
import com.aurawin.core.rsr.def.Buffers;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.rsr.def.ItemError;
import com.aurawin.core.time.Time;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.EnumSet;

public abstract class Item  implements Transport {
    public String Protocol;
    public volatile Buffers Buffers;
    protected boolean Infinite;
    protected Items Owner;
    protected SocketChannel Channel;
    protected SelectionKey Key;
    protected Date TTL;
    protected int Timeout;
    protected ItemState State;
    protected EnumSet<ItemError> Errors;

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
        Protocol = Table.String(Table.Label.Null);
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
    public String getHostName(){
        return Owner.getHostName();
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
    public void queueSend(){
        Owner.qWriteItems.add(this);
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
            int iWrite = Buffers.Recv.write(Owner.BufferRead);
            Owner.BufferRead.clear();
            return iWrite;
        } else {
            return 0;
        }
    }
    public int Write(){
        int iWritten=0;
        if (Buffers.Send.Size>0) {


            Owner.BufferWrite.clear();

            Buffers.Send.read(Owner.BufferWrite);
            Owner.BufferWrite.flip();
            while (Owner.BufferWrite.hasRemaining()) {
                try {
                    iWritten+=Channel.write(Owner.BufferWrite);
                } catch (IOException ioe){
                    return -1;
                }

            }
            Owner.BufferWrite.clear();
            Buffers.Send.sliceAtPosition();
            if (Buffers.Send.Size==0) Owner.qWriteItems.remove(this);
        } else {
            if (Buffers.Send.Size == 0) Owner.qWriteItems.remove(this);
        }
        return iWritten;
    }


}
