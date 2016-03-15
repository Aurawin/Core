package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.Security;
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

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {
    protected Boolean Infinite;
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
    protected Commands Commands;

    protected ConcurrentLinkedQueue<Item> qAddItems;
    protected ConcurrentLinkedQueue<Item> qWriteItems;
    protected ConcurrentLinkedQueue<Item> qRemoveItems;
    protected ConcurrentHashMap<SocketChannel,Item> ChannelMap;

    public ByteBuffer BufferRead;
    public ByteBuffer BufferWrite;
    public Thread Thread;
    public Engine Engine;
    public Managers Owner;

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
        Security = new Security();
    }
    @Override
    public void run() {
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
        } catch (IOException ioe){
            Syslog.Append(getClass().getCanonicalName(),"rwSelector.open", Table.Format(Table.Exception.RSR.UnableToOpenItemChannelSelector, Engine.itmRoot.getClass().getName()));
        }


        while (Engine.State!= esFinalize){
            Begin=Instant.now();
            try {
                processItems();
            } catch (IOException e){

            } finally {
                End = Instant.now();
            }
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
                        if (k.isReadable()) {
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
                                            Session ssn = Engine.Entities.Sessions.openSession();
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

}

