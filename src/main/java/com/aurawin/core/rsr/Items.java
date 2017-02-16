package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.Security;
import com.aurawin.core.rsr.def.requesthandlers.RequestHandler;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.commands.*;
import com.aurawin.core.rsr.def.sockethandlers.Handler;
import com.aurawin.core.rsr.def.sockethandlers.HandlerResult;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.time.Time;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.ItemState.*;
import static com.aurawin.core.rsr.def.ItemError.*;
import static com.aurawin.core.rsr.def.rsrResult.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {

    protected Boolean Infinite;
    public Instant Started;
    public Instant LastUsed;
    private Instant Begin;
    private Instant End;
    private HandlerResult Read;
    private HandlerResult Written;
    private rsrResult ioResult;
    private rsrResult evResult;
    private Item itm;
    private Iterator<Item> it;
    private Iterator<SelectionKey> isk;
    public Security Security;
    public Selector rwSelector;
    protected boolean RemovalRequested;
    protected Commands Commands;
    public Executor Background;

    protected ConcurrentLinkedQueue<Item> qAddItems;
    protected ConcurrentLinkedQueue<Item> qWriteItems;
    protected ConcurrentLinkedQueue<Item> qRemoveItems;

    public ByteBuffer BufferRead;
    public ByteBuffer BufferWrite;
    public Thread Thread;
    public Engine Engine;
    public Managers Owner;

    public Items(Managers aOwner, Engine aEngine, boolean aInfinite){
        super ();
        LastUsed = Instant.now();
        RemovalRequested = false;
        Infinite = aInfinite;
        Owner = aOwner;
        Engine = aEngine;
        qAddItems = new ConcurrentLinkedQueue<Item>();
        qWriteItems = new ConcurrentLinkedQueue<Item>();
        qRemoveItems = new ConcurrentLinkedQueue<Item>();
        BufferRead = ByteBuffer.allocate(Engine.BufferSizeRead);
        BufferWrite = ByteBuffer.allocate(Engine.BufferSizeWrite);
        Security = new Security();
        Commands  = new Commands(aEngine,this);
        Background = Executors.newSingleThreadExecutor();
    }
    @Override
    public void run() {
        Started = Instant.now();
        if (Engine.Security.Enabled){
            try {
                Security.setCertificate(Engine.Security.Certificate);
            } catch (UnrecoverableKeyException uke){

            } catch (CertificateException ce){

            } catch (KeyManagementException kme){

            } catch (InvalidKeySpecException ikse){

            } catch (KeyStoreException kse){

            } catch (NoSuchAlgorithmException nsae){

            }

        }

        try {
            rwSelector = java.nio.channels.Selector.open();
        } catch (Exception e){
            Syslog.Append(getClass().getCanonicalName(),"rwSelector.open", Table.Format(Table.Exception.RSR.UnableToOpenItemChannelSelector, Engine.Transport.getClass().getName()));
        }
        try {

            while ((Engine.State != esFinalize) && (RemovalRequested == false)) {
                Begin = Instant.now();
                try {
                    if (this.size() > 0) {
                        LastUsed = Instant.now();
                    }
                    processItems();
                } catch (Exception e) {
                    Syslog.Append(getClass().getCanonicalName(),"processItems",Table.Format(Table.Exception.RSR.ItemsLoop,e.getMessage()));
                } finally {
                    End = Instant.now();
                }
            }
        } finally {
            Release();
        }
    }
    private void logEntry(Item itm,String Namespace, String Unit, String Method) throws IOException {
        Syslog.Append(
                Unit,
                Method,
                Table.Format(
                        Namespace,
                        itm.SocketHandler.Channel.getLocalAddress().toString(),
                        itm.SocketHandler.Channel.getRemoteAddress().toString()
                )
        );
    }
    public int getPort(){
        return Engine.Port;
    }
    public String getHostName(){
        return Engine.HostName;
    }
    private void processItems() throws IOException{
        // process add items
        itm = qAddItems.poll();
        while (itm!=null){
            try {
                itm.Setup();
                itm.Connected();
                itm.Initialized();
                itm.State = isEstablished;
            } catch (Exception e){
                // Discard connection
                logEntry(itm, Table.Exception.RSR.UnableToRegisterItemChannel, itm.getClass().getCanonicalName(),"processItems - "+e.getMessage() );
            }
            itm=qAddItems.poll();
        }
        // process remove items
        itm = qRemoveItems.poll();
        while (itm!=null){
            itm.Teardown();
            try {
                itm.Release();
            } catch (Exception e) {

            }
            itm=qRemoveItems.poll();
        }
        try {
            if (rwSelector.selectNow() > 0) { // non blocking call
                isk = rwSelector.selectedKeys().iterator();
                while (isk.hasNext()) {
                    SelectionKey k = isk.next();
                    try {
                        if (k.isReadable() && k.isWritable() ) {
                            itm = (Item) k.attachment();
                            if (itm != null) {
                                Read = itm.SocketHandler.Recv(); //<-- buffers read into memory
                                if (Read == HandlerResult.Complete) {
                                    ioResult = itm.onPeek();
                                    switch (ioResult) {
                                        case rPostpone:
                                            itm.renewTTL();
                                            break;
                                        case rSuccess:
                                            itm.renewTTL();
                                            Session ssn = Engine.Entities.Factory.openSession();
                                            try {
                                                evResult = itm.onProcess(ssn);
                                                switch (evResult) {
                                                    case rPostpone:
                                                        itm.renewTTL();
                                                        break;
                                                    case rSuccess:
                                                        itm.renewTTL();
                                                        break;
                                                    case rFailure:
                                                        logEntry(itm, Table.Error.RSR.ProcessFailure, getClass().getCanonicalName(), "processItems -> Read -> onProcess");
                                                        qRemoveItems.add(itm);
                                                        break;
                                                }
                                            } finally {
                                                ssn.close();
                                            }
                                            break;
                                        case rFailure:
                                            logEntry(itm, Table.Error.RSR.PeekFailure, getClass().getCanonicalName(), "processItems -> Read -> onPeek");
                                            qRemoveItems.add(itm);
                                            break;
                                    }

                                } else if (itm.SocketHandler.Channel.isOpen() == false) {
                                    itm.Errors.add(eReset);
                                    itm.Error();
                                    qRemoveItems.add(itm);
                                }
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
            if ( (itm.TTL!=null) && (itm.Timeout>0) && Begin.isAfter(itm.TTL)){
                itm.Errors.add(eTimeout);
                itm.Error();
                qRemoveItems.add(itm);
            }
        }
        it = qWriteItems.iterator();
        while (it.hasNext()) {
            itm=it.next();
            Written=itm.SocketHandler.Send();
            switch (Written) {
                case Complete:
                    break;
                case Pending:
                    break;
                case Failure:
                    itm.Errors.add(eReset);
                    itm.Errors.add(eWrite);
                    qRemoveItems.add(itm);
                    qWriteItems.remove(itm);
                    itm.Error();
                    break;
            }
        }
        try {
            java.lang.Thread.sleep(Settings.RSR.Server.ManagerYield);
        } catch (InterruptedException ie){
            // do nothing
        }
    }
    public void adjustReadBufferSize() throws Exception{
        if (java.lang.Thread.currentThread().equals(this)==true){
            BufferRead.clear();
            BufferRead=ByteBuffer.allocate(Engine.BufferSizeRead);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.UnableToAccessConncurrently));
        }
    }

    public void adjustWriteBufferSize() throws Exception{
        if (java.lang.Thread.currentThread().equals(this)==true){
            BufferWrite.clear();
            BufferWrite=ByteBuffer.allocate(Engine.BufferSizeWrite);
        } else {
            throw new Exception(Table.String(Table.Exception.RSR.UnableToAccessConncurrently));
        }
    }
    public void removeFromWriteQueue(Item item){
        qWriteItems.remove(item);
    }
    public void scheduleRemoval(Item item){
        qRemoveItems.add(item);
    }

    public void Release(){
        Owner.remove(this);
        Started=null;
        LastUsed=null;
        Begin=null;
        End=null;
        Read=null;
        Written=null;
        ioResult=null;
        evResult=null;
        itm=null;
        it=null;
        isk=null;
        Security.Release();
        Security=null;
        rwSelector=null;

        Commands.Release();
        Commands=null;

        qAddItems.clear();
        qAddItems=null;
        qWriteItems.clear();
        qWriteItems=null;
        qRemoveItems.clear();
        qRemoveItems=null;

        BufferRead=null;
        BufferWrite=null;
        Thread=null;
        Engine=null;
        Owner=null;

        Background=null;
    }

}

