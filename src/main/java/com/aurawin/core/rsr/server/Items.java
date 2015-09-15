package com.aurawin.core.rsr.server;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.server.ServerState;
import com.aurawin.core.rsr.def.server.rsrResult;
import com.aurawin.core.rsr.server.Commands.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;

import static com.aurawin.core.rsr.def.server.ItemState.*;
import static com.aurawin.core.rsr.def.server.rsrResult.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {
    protected Boolean Infinite;
    private Date dtBegin;
    private Date dtEnd;
    private int ioRead;
    private int ioWrite;
    private rsrResult ioResult;
    private rsrResult pcResult;
    protected Engine Engine;
    protected Managers Owner;
    protected Selector csSelector;
    protected Commands Commands;
    protected ConcurrentLinkedQueue<Item> qAddItems;
    protected ConcurrentLinkedQueue<Item> qRemoveItems;
    protected ConcurrentHashMap<SocketChannel,Item> ChannelMap;
    protected ByteBuffer BufferRead;
    protected ByteBuffer BufferWrite;
    public Items(Managers aOwner, Engine aEngine){
        super ();
        Owner = aOwner;
        Engine = aEngine;
        qAddItems = new ConcurrentLinkedQueue<Item>();
        qRemoveItems = new ConcurrentLinkedQueue<Item>();
        BufferRead = ByteBuffer.allocate(Engine.BufferSizeRead);
        BufferWrite = ByteBuffer.allocate(Engine.BufferSizeWrite);

        try {
            csSelector = Selector.open();
        } catch (IOException ioe){
            Syslog.Append(getClass().getCanonicalName(),"Selector.open", Table.Format(Table.Exception.RSR.Server.UnableToOpenItemChannelSelector, Engine.itmclass.getName()));
        }

    }
    @Override
    public void run() {
        while (Engine.state!= ServerState.ssFinalize){
            dtBegin=new Date();
            processItems();
            dtEnd=new Date();
            // now we can compare at sort/remove/shrink array
        }
    }
    private void logEntry(Item itm,String Namespace, String Unit, String Method){
        try {
            Syslog.Append(
                    Unit,
                    Method,
                    Table.Format(
                            Namespace,
                            itm.Channel.getLocalAddress().toString(),
                            itm.Channel.getRemoteAddress().toString()
                    )
            );
        } catch (IOException ioe){
            // do nothing
        }
    }
    private void processItems(){
        // process add items
        Item itm = qAddItems.poll();
        while (itm!=null){
            try {
                itm.Channel.configureBlocking(false);
                itm.Key=itm.Channel.register(csSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, itm);
                itm.TTL= Time.incMilliSeconds(new Date(), Settings.RSR.Server.Timeout);
                add(itm);
                if (itm.onAccepted()==rSuccess){
                    if (itm.onInitialize()==rSuccess){
                        itm.state=isEstablished;
                    } else {
                        qRemoveItems.add(itm);
                        logEntry(itm, Table.Error.RSR.Server.InitializeFailure, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
                    }
                } else {
                    qRemoveItems.add(itm);
                    logEntry(itm, Table.Error.RSR.Server.AcceptFailure, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
                }
            } catch (Exception e){
                // Discard connection
                logEntry(itm, Table.Exception.RSR.Server.UnableToRegisterItemChannel, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
            }
            itm=qAddItems.poll();
        }
        // process add items
        itm = qRemoveItems.poll();
        while (itm!=null){
            try {
                itm.Channel.close();
            } catch (Exception e){
                // Discard connection
                logEntry(itm, Table.Exception.RSR.Server.UnableToCloseItemChannel, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
            }
            if (itm.onDisconnected()==rSuccess){
                itm.state=isFinalize;
            } else {
                logEntry(itm, Table.Error.RSR.Server.DisconnectFailure, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
            }
            if (itm.onFinalize()==rSuccess){
                itm.state=isNone;
                remove(itm);
            } else {
                logEntry(itm, Table.Error.RSR.Server.FinalizeFailure, getClass().getCanonicalName(), getClass().getEnclosingMethod().getName());
            }
            itm=qAddItems.poll();
        }
        // Find sockets to read
        try {
            if (csSelector.selectNow() > 0) { // non blocking call
                Iterator<SelectionKey> it = csSelector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey k = it.next();
                    try {
                        itm = (Item) k.attachment();
                        if (itm!=null){
                            ioRead=itm.Read(); //<-- buffers read into memory
                            if (ioRead>0) {
                                ioResult=itm.onPeek();
                                switch (ioResult) {
                                    case rPostpone :
                                        itm.renewTTL();
                                        break;
                                    case rSuccess :
                                        itm.renewTTL();
                                        pcResult=itm.onProcess();
                                        switch (pcResult){
                                            case rPostpone:
                                                itm.renewTTL();
                                                break;
                                            case rSuccess:
                                                itm.Buffers.Read.sliceAtPosition();
                                                itm.renewTTL();
                                                break;
                                            case rFailure:

                                        }

                                        break;
                                    case rFailure :
                                        logEntry(itm,Table.Error.RSR.Server.PeekFailure,getClass().getCanonicalName(),getClass().getEnclosingMethod().getName());
                                        break;
                                }

                            } else {
                                // Connection was reset
                                // todo process item teardown
                            }
                        }
                    } finally {
                        it.remove();
                    }

                }
            }
        } catch (IOException ie){

        }

    }
    public void adjustReadBufferSize() throws Exception{
        if (Thread.currentThread().equals(this)==true){
            BufferRead.clear();
            BufferRead=ByteBuffer.allocate(Engine.BufferSizeRead);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.Server.UnableToAccessConncurrently));
        }
    }
    public void adjustWriteBufferSize() throws Exception{
        if (Thread.currentThread().equals(this)==true){
            BufferWrite.clear();
            BufferWrite=ByteBuffer.allocate(Engine.BufferSizeWrite);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.Server.UnableToAccessConncurrently));
        }
    }

}

