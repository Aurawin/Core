package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.Persist;
import com.aurawin.core.rsr.def.TransportConnect;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.def.handlers.SocketHandlerResult;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.ItemKind.Server;
import static com.aurawin.core.rsr.def.ItemState.*;
import static com.aurawin.core.rsr.def.ItemError.*;

import java.io.IOException;
import java.net.ConnectException;
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
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {

    protected Boolean Infinite;
    public Instant Started;
    public Instant LastUsed;
    private Instant Begin;
    private Instant End;
    private SocketHandlerResult Read;
    private SocketHandlerResult Written;
    private rsrResult ioResult;
    private rsrResult evResult;
    private Item itm;
    private TransportConnect tcItem;
    private TransportConnect tcNextItem;
    private Iterator<Item> it;
    private Iterator<SelectionKey> isk;

    public Selector Keys;
    public Security Security;
    protected boolean RemovalRequested;

    public Executor Background;
    protected ConcurrentLinkedQueue<TransportConnect> qRequestConnect;
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
        qRequestConnect = new ConcurrentLinkedQueue<TransportConnect>();
        BufferRead = ByteBuffer.allocate(Engine.BufferSizeRead);
        BufferWrite = ByteBuffer.allocate(Engine.BufferSizeWrite);
        Security = new Security();

        Background = Executors.newSingleThreadExecutor();
    }
    @Override
    public void run() {
        Started = Instant.now();
        if (Engine.SSL.Enabled){
            try {
                Security.Load(Engine.SSL.getCertificate());
            } catch (IOException ioe){

            } catch (UnrecoverableKeyException uke){

            } catch (CertificateException ce){

            } catch (KeyManagementException kme){

            } catch (InvalidKeySpecException ikse){

            } catch (KeyStoreException kse){

            } catch (NoSuchAlgorithmException nsae){

            }

        }

        try {
            Keys = java.nio.channels.Selector.open();
        } catch (Exception e){
            Syslog.Append(getClass().getCanonicalName(),"Selector.open", Table.Format(Table.Exception.RSR.UnableToOpenItemChannelSelector, Engine.transportClass.getName()));
        }
        try {

            while ((Engine.State != esFinalize) && (!RemovalRequested)) {
                Begin = Instant.now();
                try {
                    if (size() > 0) {
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
    private void processItems() throws IOException{
        // process add items
        itm = qAddItems.poll();
        while (itm!=null){
            try {
                itm.Setup();
                itm.Initialized();
                if (itm.Kind==Server){
                    itm.State = isEstablished;
                }
            } catch (Exception e){
                // Discard connection
                logEntry(itm, Table.Exception.RSR.UnableToRegisterItemChannel, itm.getClass().getCanonicalName(),"processItems - "+e.getMessage() );
            }
            itm=qAddItems.poll();
        }
        // process client connect requests
        tcItem = qRequestConnect.poll();
        tcNextItem = null;
        while (tcItem!=null) {
            if (tcItem.readyToConnect()) {
                SocketChannel aChannel = SocketChannel.open();
                try {
                    if (!tcItem.hasOwner()) {
                        itm = (Item) tcItem.getMethod().invoke(
                                tcItem.getObject(),
                                this,
                                aChannel,
                                ItemKind.Client
                        );

                        itm.Address = tcItem.getAddress();
                        itm.bindAddress = Engine.Address;
                        itm.Kind = ItemKind.Client;
                        itm.connectionData = tcItem;
                        tcItem.setOwner(itm);

                    }
                    itm = tcItem.getOwnerOrWait();
                    itm.setChannel(aChannel);

                    if (tcItem.getAddress() != null) {
                        try {
                            if (itm.bindAddress != null) {
                                aChannel.bind(itm.bindAddress);
                            }
                            aChannel.configureBlocking(true);
                            try {
                                tcItem.attemptConnect();
                                aChannel.connect(itm.Address);
                                qAddItems.add(itm);
                                aChannel.configureBlocking(false);
                                tcItem.resetTrys();
                            } catch (Exception e) {
                                tcItem.incTry();
                                if (tcItem.getTries() < Settings.RSR.Items.TransportConnect.MaxTries) {
                                    qRequestConnect.add(tcItem);
                                    aChannel.close();
                                } else {
                                }
                                itm.setChannel(null);
                                Syslog.Append(getClass().getCanonicalName(), "processItems.Connect", Table.Format(Table.Exception.RSR.ManagerConnect, e.getMessage(), tcItem.getAddress().getHostString()));
                            }
                        } catch (Exception e) {
                            aChannel.close();
                            Syslog.Append(getClass().getCanonicalName(), "processItems.Connect.Bind", Table.Format(Table.Exception.RSR.ManagerConnectWithBind, e.getMessage(), Engine.Address.toString(), tcItem.getAddress().toString()));
                        }
                    }

                } catch (Exception e) {
                    aChannel.close();
                    Syslog.Append(getClass().getCanonicalName(), "processItems.Connect.Constructor", Table.Format(Table.Exception.RSR.ManagerConnectConstructor, e.getMessage(), tcItem.getAddress().toString()));

                }
            } else if (tcItem.exceededTrys()==false) {
                qRequestConnect.add(tcItem);
            }
            tcNextItem = qRequestConnect.poll();
            if (tcItem == tcNextItem) {
                tcItem = qRequestConnect.poll();
                qRequestConnect.add(tcNextItem);
            } else {
                tcItem = tcNextItem;
            }
        }
        // process remove items
        itm = qRemoveItems.poll();
        while (itm!=null) {
            TransportConnect tcData= itm.getConnectionData();
            if (tcData!=null) {
                if (!tcData.exceededTrys()) {
                    qRequestConnect.add(tcData);
                    break;
                }
            }
            itm.Teardown();
            try {
                    itm.Release();
            } catch (Exception e) {
            }
            itm=qRemoveItems.poll();
        }
        try {
            if (Keys.selectNow() > 0) { // non blocking call
                isk = Keys.selectedKeys().iterator();
                while (isk.hasNext()) {
                    SelectionKey k = isk.next();
                    try {
                        if (
                                ((k.readyOps() & SelectionKey.OP_CONNECT) != 0) &&
                                ((k.readyOps() & SelectionKey.OP_WRITE)!=0)&&
                                ((k.readyOps() & SelectionKey.OP_READ)!=0)
                           )

                        {
                            itm = (Item) k.attachment();
                            if (itm != null) {
                                try {
                                    if (itm.SocketHandler.Channel.finishConnect()) {
                                        if (itm.SocketHandler.Channel.isConnected()) {
                                            if (Security.Enabled==true) {
                                                itm.SocketHandler.beginHandshake();
                                            } else {
                                                qAddItems.add(itm);
                                                itm.State = isEstablished;
                                                itm.Connected();
                                            }
                                            itm.renewTTL();
                                        } else {
                                            itm.Errors.add(eConnect);
                                            itm.Error();
                                            qRemoveItems.add(itm);
                                        }
                                    }
                                } catch (ConnectException ex) {
                                    itm.Errors.add(eConnect);
                                    if (ex.getMessage().equalsIgnoreCase("connection refused")){
                                        itm.State = isRefused;
                                    }else if (ex.getMessage().equalsIgnoreCase("connection timeout")){
                                        itm.State = isTimed;
                                    }
                                    itm.Error();
                                    qRemoveItems.add(itm);
                                }
                            }
                        } else if ((k.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_WRITE) ) != 0) {
                            itm = (Item) k.attachment();
                            if (itm != null) {
                                Read = itm.SocketHandler.Recv(); //<-- buffers read into memory
                                if (Read == SocketHandlerResult.Complete) {
                                    ioResult = itm.onPeek();
                                    switch (ioResult) {
                                        case rPostpone:
                                            itm.renewTTL();
                                            break;
                                        case rSuccess:
                                            itm.renewTTL();
                                            Session ssn = Entities.openSession();
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
                                                itm.Reset();
                                            } finally {
                                                ssn.close();
                                            }
                                            break;
                                        case rFailure:
                                            logEntry(itm, Table.Error.RSR.PeekFailure, getClass().getCanonicalName(), "processItems -> Read -> onPeek");
                                            qRemoveItems.add(itm);
                                            break;
                                    }

                                } else if (Read == SocketHandlerResult.Failure) {
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
            if ((itm.TTL!=null) && (itm.Timeout>0) && Begin.isAfter(itm.TTL)){
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
                    qWriteItems.remove(itm);
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
            it =null;

        }
    }
    protected void adjustReadBufferSize() throws Exception{
        if (java.lang.Thread.currentThread().equals(this)==true){
            ByteBuffer BufferNew = ByteBuffer.allocate(Engine.BufferSizeRead);
            BufferNew.put(BufferRead);
            BufferRead = BufferNew;

        } else {
            throw new Exception(Table.String(Table.Exception.RSR.UnableToAccessConncurrently));
        }
    }

    protected void adjustWriteBufferSize() throws Exception{
        if (java.lang.Thread.currentThread().equals(this)==true){
            ByteBuffer BufferNew = ByteBuffer.allocate(Engine.BufferSizeWrite);
            BufferNew.put(BufferWrite);
            BufferWrite=BufferNew;
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
        stream().forEach(i-> i.Release());
        qAddItems.stream().forEach(i-> i.Release());
        qRequestConnect.stream().forEach(tc-> tc.Release());
        qRemoveItems.stream().forEach(i-> i.Release());
        qWriteItems.stream().forEach(i->i.Release());

        if (Keys!=null) {
            try {
                Keys.close();
            } catch (Exception ex){

            }
        }

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
        Keys=null;

        qRequestConnect.clear();
        qRequestConnect=null;

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

