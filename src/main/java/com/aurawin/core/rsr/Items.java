package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.Commands.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.ItemState.*;
import static com.aurawin.core.rsr.def.ItemError.*;
import static com.aurawin.core.rsr.def.rsrResult.*;

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
    private rsrResult evResult;
    private Item itm;
    private Iterator<Item> it;
    private Iterator<SelectionKey> isk;
    protected Thread Thread;
    protected Engine Engine;
    protected Managers Owner;
    protected Selector csSelector;
    protected Commands Commands;
    protected ConcurrentLinkedQueue<Item> qAddItems;
    protected ConcurrentLinkedQueue<Item> qWriteItems;
    protected ConcurrentLinkedQueue<Item> qRemoveItems;
    protected ConcurrentHashMap<SocketChannel,Item> ChannelMap;
    protected ByteBuffer BufferRead;
    protected ByteBuffer BufferWrite;
    public Items(Managers aOwner, Engine aEngine, boolean aInfinite){
        super ();
        Infinite = aInfinite;
        Owner = aOwner;
        Engine = aEngine;
        qAddItems = new ConcurrentLinkedQueue<Item>();
        qWriteItems = new ConcurrentLinkedQueue<Item>();
        qRemoveItems = new ConcurrentLinkedQueue<Item>();
        BufferRead = ByteBuffer.allocate(Engine.BufferSizeRead);
        BufferWrite = ByteBuffer.allocate(Engine.BufferSizeWrite);

        try {
            csSelector = Selector.open();
        } catch (IOException ioe){
            Syslog.Append(getClass().getCanonicalName(),"Selector.open", Table.Format(Table.Exception.RSR.UnableToOpenItemChannelSelector, Engine.itmRoot.getClass().getName()));
        }

    }
    @Override
    public void run() {
        while (Engine.State!= esFinalize){
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
        itm = qAddItems.poll();
        while (itm!=null){
            try {
                itm.Channel.configureBlocking(false);
                itm.Key=itm.Channel.register(csSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, itm);
                itm.TTL= Time.incMilliSeconds(new Date(), Settings.RSR.Server.Timeout);
                add(itm);
                if (itm.onAccepted()==rSuccess){
                    if (itm.onInitialize()==rSuccess){
                        itm.State=isEstablished;
                    } else {
                        qRemoveItems.add(itm);
                        logEntry(itm, Table.Error.RSR.InitializeFailure, itm.getClass().getCanonicalName(), "processItems.onInitialize");
                    }
                } else {
                    qRemoveItems.add(itm);
                    logEntry(itm, Table.Error.RSR.AcceptFailure, itm.getClass().getCanonicalName(), "processItems.onAccepted");
                }
            } catch (Exception e){
                // Discard connection
                logEntry(itm, Table.Exception.RSR.UnableToRegisterItemChannel, itm.getClass().getCanonicalName(),"processItems - "+e.getMessage() );
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
                logEntry(itm, Table.Exception.RSR.UnableToCloseItemChannel, getClass().getCanonicalName(), "processItems -> removeItems -> close channel");
            }
            if (itm.onDisconnected()==rSuccess){
                itm.State=isFinalize;
            } else {
                logEntry(itm, Table.Error.RSR.DisconnectFailure, getClass().getCanonicalName(), "processItems -> removeItems -> onDisconnected");
            }
            if (itm.onFinalize()==rSuccess){
                itm.State=isNone;
            } else {
                logEntry(itm, Table.Error.RSR.FinalizeFailure, getClass().getCanonicalName(),  "processItems -> removeItems -> onFinalize");
            }
            remove(itm);
            itm=qAddItems.poll();
        }
        // Find sockets to read
        try {
            if (csSelector.selectNow() > 0) { // non blocking call
                isk = csSelector.selectedKeys().iterator();
                while (isk.hasNext()) {
                    SelectionKey k = isk.next();
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
                                        evResult=itm.onProcess();
                                        switch (evResult){
                                            case rPostpone:
                                                itm.renewTTL();
                                                break;
                                            case rSuccess:
                                                itm.renewTTL();
                                                break;
                                            case rFailure:
                                                logEntry(itm, Table.Error.RSR.ProcessFailure, getClass().getCanonicalName(),  "processItems -> Read -> onProcess");
                                                itm.Teardown();
                                                break;
                                        }
                                        break;
                                    case rFailure :
                                        logEntry(itm,Table.Error.RSR.PeekFailure,getClass().getCanonicalName(), "processItems -> Read -> onPeek");
                                        itm.Teardown();
                                        break;
                                }

                            } else {
                                itm.Errors.add(eReset);
                                evResult=itm.onError();
                                itm.Teardown();
                            }
                        }
                    } finally {
                        isk.remove();
                    }

                }
            }
        } catch (IOException ie){
            Syslog.Append("Items","processItems",Table.String(Table.Exception.RSR.UnableToSelectItemKeys));
        }
        // Process Timeout Checks on all sockets
        it=iterator();
        while (it.hasNext()) {
            itm=it.next();
            if ( (itm.TTL!=null) && (itm.Timeout>0) && dtBegin.after(itm.TTL)){
                itm.Errors.add(eTimeout);
                evResult=itm.onError();
                switch (evResult){
                    case rPostpone:
                        itm.Errors.remove(eTimeout);
                        itm.renewTTL();
                        break;
                    case rSuccess:
                        itm.Teardown();
                        break;
                    case rFailure:
                        logEntry(itm,Table.Error.RSR.Timeout,getClass().getCanonicalName(), "processItems -> Timeout -> onError");
                        itm.Teardown();
                        break;
                }
            }
        }
        it = qWriteItems.iterator();
        while (it.hasNext()) {
            itm=it.next();
            ioWrite=itm.Write();
            if (ioWrite==-1) {
                // Remote channel was reset
                itm.Errors.add(eReset);
                itm.Errors.add(eWrite);
                evResult=itm.onError();
                if (evResult==rFailure)
                    logEntry(itm,Table.Error.RSR.Write,getClass().getCanonicalName(), "processItems -> Write -> onError");
                itm.Teardown();
            }

        }
        try {
            Thread.sleep(Settings.RSR.Server.ManagerYield);
        } catch (InterruptedException ie){
            // do nothing
        }
    }
    public void adjustReadBufferSize() throws Exception{
        if (Thread.currentThread().equals(this)==true){
            BufferRead.clear();
            BufferRead=ByteBuffer.allocate(Engine.BufferSizeRead);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.UnableToAccessConncurrently));
        }
    }
    public void adjustWriteBufferSize() throws Exception{
        if (Thread.currentThread().equals(this)==true){
            BufferWrite.clear();
            BufferWrite=ByteBuffer.allocate(Engine.BufferSizeWrite);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.UnableToAccessConncurrently));
        }
    }

}

