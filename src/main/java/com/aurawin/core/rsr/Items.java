package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.*;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.def.handlers.SocketHandlerResult;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.ItemCommand.*;
import static com.aurawin.core.rsr.def.ItemKind.Client;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Items  implements Runnable {
    protected volatile ArrayList<Item>List = new ArrayList<>();
    private ArrayList<Item> removalItems = new ArrayList<>();
    private volatile Item processItem;
    protected Boolean Infinite;
    public Instant Started;
    public Instant LastUsed;
    private Instant Begin;
    private Instant End;
    private SocketHandlerResult Read;
    private SocketHandlerResult Written;
    private rsrResult ioResult;
    private rsrResult evResult;

    private Iterator<SelectionKey> isk;

    public Selector Keys;
    public Security Security;
    protected boolean elasticRebound;

    public Executor Background;

    public ByteBuffer BufferRead;
    public ByteBuffer BufferWrite;
    public Thread Thread;
    public Engine Engine;
    public Managers Owner;

    public static Gson gsonParser;
    private static GsonBuilder gsonBuilder;

    public Items(Managers aOwner, Engine aEngine, boolean aInfinite){
        LastUsed = Instant.now();
        elasticRebound = false;
        Infinite = aInfinite;
        Owner = aOwner;
        Engine = aEngine;

        BufferRead = ByteBuffer.allocate(Engine.BufferSizeRead);
        BufferWrite = ByteBuffer.allocate(Engine.BufferSizeWrite);
        Security = new Security();

        Background = Executors.newSingleThreadExecutor();
        gsonBuilder = new GsonBuilder();
        gsonParser = gsonBuilder.create();

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

            while ((Engine.State != esStop) && (!elasticRebound)) {
                Begin = Instant.now();
                try {
                    if (List.size() > 0) {
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
    private void logEntry(Item itm,String Namespace, String Unit, String Method) {
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
        } catch (Exception ex){

        }
    }
    private void processAccept(){
        processItem.Commands.remove(cmdAccept);
        processItem.Commands.add(cmdSetup);
        processItem.Connected();
        System.out.println("Items.processAccept()");
    }
    private void processConnect(){
        processItem.Commands.remove(cmdConnect);

        if (processItem.readyToConnect()) {
            try {
                processItem.State=isNone;
                processItem.Errors.clear();
                processItem.reAllocateChannel();
                try {
                    processItem.renewTTL();
                    processItem.renewRetryTLL();

                    processItem.Channel.connect(processItem.Address);
                    /*
                    if (processItem.Channel.connect(processItem.Address)) {

                        processItem.State = isConnecting;
                        processItem.resetTrys();
                    } else {
                        processItem.State = isRefused;
                        processItem.keyConnect.cancel();
                        processItem.Channel.close();
                        processItem.incTrys();
                        processItem.Commands.add(cmdConnect);
                    }*/
                    processItem.resetTrys();
                    processItem.State = isConnecting;
                    processItem.Commands.add(cmdPoll);
                } catch (Exception e) {
                    processItem.incTrys();
                    processItem.State=isRefused;
                    if (processItem.allowedToRetry()){
                        processItem.renewRetryTLL();
                        processItem.Commands.add(cmdConnect);
                    } else {
                        processItem.Errors.add(eConnect);
                        processItem.Commands.add(cmdError);
                        processItem.Commands.add(cmdTeardown);

                        Syslog.Append(
                                getClass().getCanonicalName(),
                                "processItems.Connect",
                                Table.Format(Table.Exception.RSR.ManagerConnect, e.getMessage(),
                                        processItem.Address.getHostString())
                        );
                    }
                }
            } catch (Exception e) {
                processItem.Errors.add(eConnect);
                processItem.Commands.add(cmdError);
                processItem.Commands.add(cmdTeardown);

                Syslog.Append(
                        getClass().getCanonicalName(),
                        "processItems.Connect.Bind",
                        Table.Format(Table.Exception.RSR.ManagerConnectWithBind, e.getMessage(),Engine.Address.toString(), processItem.Address.toString())
                );
            }


        } else if (processItem.allowedToRetry()){
            processItem.Commands.add(cmdConnect);
        } else{
            // too many attempts to connect
            processItem.Errors.add(eConnect);
            processItem.Commands.remove(cmdConnect);
            processItem.Commands.add(cmdError);
            processItem.Commands.add(cmdTeardown);
        }
    }

    private void processSend(){
        processItem.renewTTL();
        processItem.renewRetryTLL();
        processItem.SocketHandler.Send();
        if (processItem.Buffers.Send.Size==0)
            processItem.Commands.remove(cmdSend);
    }

    private void processPoll(){
        try {
            if (Keys.selectNow() > 0) { // non blocking call
                isk = Keys.selectedKeys().iterator();
                while (isk.hasNext()) {
                    SelectionKey k = isk.next();
                    try {
                        if (
                                ((k.readyOps() & SelectionKey.OP_CONNECT) != 0)
                                )

                        {
                            Item itm = (Item) k.attachment();
                            processItem = itm;
                            if (itm != null) {
                                processItem.renewTTL();
                                processItem.renewRetryTLL();

                                try {
                                    if (itm.Channel.finishConnect()) {
                                        if (itm.Channel.isConnected()) {
                                            itm.Commands.add(cmdSetup);
                                            itm.Commands.remove(cmdPoll);
                                            itm.renewTTL();
                                        } else {
                                            itm.Errors.add(eConnect);
                                            itm.Commands.add(cmdError);
                                            itm.Commands.add(cmdTeardown);
                                            itm.Commands.remove(cmdPoll);
                                        }
                                    }
                                } catch (ConnectException ex) {
                                    itm.Errors.add(eConnect);
                                    if (ex.getMessage().equalsIgnoreCase("connection refused")){
                                        itm.State = isRefused;
                                    } else if (ex.getMessage().equalsIgnoreCase("connection timeout")){
                                        itm.State = isTimed;
                                    }
                                    itm.Errors.add(eConnect);
                                    itm.Commands.add(cmdError);
                                    itm.Commands.add(cmdTeardown);
                                    itm.Commands.remove(cmdPoll);
                                }
                            }
                        } else if ((k.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_WRITE) ) != 0) {
                            Item itm = (Item) k.attachment();
                            processItem=itm;
                            if (itm != null) {
                                processItem.renewTTL();
                                processItem.renewRetryTLL();
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
                                                        itm.Errors.add(eRead);
                                                        itm.Commands.add(cmdError);
                                                        itm.Commands.add(cmdTeardown);
                                                        itm.Commands.remove(cmdPoll);
                                                        break;
                                                }
                                                if (itm.Kind==Server) itm.Reset();
                                            } finally {
                                                ssn.close();
                                            }

                                            break;
                                        case rFailure:
                                            logEntry(itm, Table.Error.RSR.PeekFailure, getClass().getCanonicalName(), "processItems -> Read -> onPeek");
                                            itm.Errors.add(eRead);
                                            itm.Commands.add(cmdError);
                                            itm.Commands.add(cmdTeardown);
                                            itm.Commands.remove(cmdPoll);
                                            break;
                                    }

                                } else if (Read == SocketHandlerResult.Failure) {
                                    itm.Errors.add(eReset);
                                    itm.Commands.add(cmdError);
                                    itm.Commands.add(cmdTeardown);
                                    itm.Commands.remove(cmdPoll);
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
    }

    private void processError(){
        processItem.Commands.remove(cmdError);
        processItem.Error();
    }
    private void processTeardown(){
        processItem.Commands.remove(cmdTeardown);
        removalItems.add(processItem);
        processItem.Disconnected();
        processItem.Teardown();


        if (processItem.Persistent!=null) {
            if (processItem.Persistent.exceededTrys()) {
                processItem.Finalized();
                processItem.State = isFinalize;
            } else if (processItem.Kind==Client){
                processItem.Commands.add(cmdConnect);
            }
        } else {
            processItem.Finalized();
            processItem.State = isFinalize;
        }
    }
    private void processSetup(){
        try {
            processItem.Commands.remove(cmdSetup);
            processItem.Setup();
            processItem.Initialized();

            if (processItem.Errors.isEmpty()){
                processItem.Commands.add(cmdPoll);
                if (processItem.Kind==Server){
                    processItem.State = isEstablished;
                    processItem.Commands.add(cmdPoll);
                }
            } else{
                processItem.Commands.add(cmdError);

            }

        } catch (Exception e){
            Syslog.Append("Items", "processSetup", e.getMessage());
        }
    }

    private void processItems() throws IOException{
        Iterator<Item> it = List.iterator();
        while (it.hasNext()) {
            processItem=it.next();
            for (ItemCommand c:processItem.Commands){
                switch (c) {
                    case cmdAccept: processAccept(); break;
                    case cmdConnect: processConnect(); break;
                    case cmdSend: processSend(); break;
                    case cmdPoll: processPoll(); break;
                    case cmdError: processError(); break;
                    case cmdSetup: processSetup(); break;
                    case cmdTeardown: processTeardown(); break;
                }
            }
        }
        List.removeAll(removalItems);
        removalItems.clear();
        try {
            java.lang.Thread.sleep(Settings.RSR.Server.ManagerYield);
        } catch (InterruptedException ie){
            Syslog.Append("Items", "processItems", ie.getMessage());
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

    public void Release(){
        List.stream().forEach(i-> i.Release());


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

        isk=null;
        Security.Release();
        Security=null;
        Keys=null;

        BufferRead=null;
        BufferWrite=null;
        Thread=null;
        Engine=null;
        Owner=null;

        Background=null;
    }



}

