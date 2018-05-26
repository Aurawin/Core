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
import static com.aurawin.core.rsr.def.handlers.SocketHandlerResult.Complete;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
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
        Thread = java.lang.Thread.currentThread();
        Thread.setPriority(Settings.RSR.Items.ThreadPriorityNormal);
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

                    Syslog.Append(getClass().getCanonicalName(),"processItems",Table.Format(Table.Exception.RSR.ItemsLoop,e.toString()));
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
        try {
            processItem.Channel.configureBlocking(false);
            processItem.keySelect = processItem.Channel.register(Keys, OP_READ | OP_WRITE, processItem);
            processItem.Commands.remove(cmdAccept);
            processItem.bindAddress=Engine.Address;
            processItem.Commands.add(cmdSetup);
            processItem.Connected();
        } catch (ClosedChannelException cce){
            processItem.Commands.add(cmdError);
            processItem.Commands.add(cmdTeardown);
        } catch (IOException ioe){
            processItem.Commands.add(cmdError);
            processItem.Commands.add(cmdTeardown);
        }


    }
    private void processConnect(){
        processItem.Commands.remove(cmdConnect);

        if (processItem.readyToConnect()) {
            try {
                processItem.incTrys();
                processItem.State=isNone;
                processItem.Errors.clear();
                processItem.reAllocateChannel();
                try {
                    processItem.renewTTL();
                    processItem.Channel.connect(processItem.Address);

                    processItem.renewTTL();
                    processItem.State = isConnecting;
                    processItem.Commands.add(cmdPoll);
                } catch (Exception e) {
                    processItem.incTrys();
                    processItem.State=isRefused;
                    if (processItem.exceededTrys()){
                        processItem.renewTTL();
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


        } else if (!processItem.exceededTrys()){
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
        Thread.setPriority(Settings.RSR.Items.ThreadPriorityHigh);
        try {

            switch (processItem.SocketHandler.Send()){
                case Complete:
                    if (!processItem.SocketHandler.hasBytesToSend()) processItem.Commands.remove(cmdSend);
                    break;
                case Failure:
                    break;
                case Pending:
                    // dataToSend is already set
                    break;
            }

        } finally {
            Thread.setPriority(Settings.RSR.Items.ThreadPriorityNormal);
        }

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

                            processItem = (Item) k.attachment();
                            if (processItem != null) {
                                processItem.renewTTL();
                                try {
                                    if (processItem.Channel.finishConnect()) {
                                        if (processItem.Channel.isConnected()) {
                                            processItem.Commands.add(cmdSetup);
                                            processItem.Commands.remove(cmdPoll); // ssl needs this
                                            processItem.renewTTL();
                                        } else {
                                            processItem.Errors.add(eConnect);
                                            processItem.Commands.add(cmdError);
                                            processItem.Commands.add(cmdTeardown);
                                            processItem.Commands.remove(cmdPoll);
                                        }
                                    }
                                } catch (ConnectException ex) {
                                    processItem.Errors.add(eConnect);
                                    if (ex.getMessage().equalsIgnoreCase("connection refused")) {
                                        processItem.State = isRefused;
                                    } else if (ex.getMessage().equalsIgnoreCase("connection timeout")) {
                                        processItem.State = isTimed;
                                    }
                                    processItem.Errors.add(eConnect);
                                    processItem.Commands.add(cmdError);
                                    processItem.Commands.add(cmdTeardown);
                                    processItem.Commands.remove(cmdPoll);
                                }
                            }
                        }
                        if ( k.isValid() && (k.readyOps() & OP_WRITE)!=0) {
                            processItem=(Item) k.attachment();
                            processItem.Commands.add(cmdSend);
                        }
                        if ( k.isValid() && (k.readyOps() & OP_READ)!=0 ) {
                            processItem=(Item) k.attachment();
                            processItem.Commands.add(cmdRecv);
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
    private void processRecv(){
        Thread.setPriority(Settings.RSR.Items.ThreadPriorityHigh);
        try {
            Read = processItem.SocketHandler.Recv();
            switch (Read){
                case Complete :
                    ioResult = processItem.onPeek();
                    switch (ioResult) {
                        case rPostpone:
                            processItem.renewTTL();
                            break;
                        case rSuccess:
                            processItem.renewTTL();

                            Session ssn = Entities.openSession();
                            try {
                                evResult = processItem.onProcess(ssn);
                                switch (evResult) {
                                    case rPostpone:
                                        processItem.renewTTL();
                                        break;
                                    case rSuccess:
                                        processItem.renewTTL();
                                        break;
                                    case rFailure:
                                        logEntry(processItem, Table.Error.RSR.ProcessFailure, getClass().getCanonicalName(), "processItems -> Read -> onProcess");
                                        processItem.Errors.add(eRead);
                                        processItem.Commands.add(cmdError);
                                        processItem.Commands.add(cmdTeardown);
                                        processItem.Commands.remove(cmdPoll);
                                        break;
                                }
                                if (processItem.Kind == Server) processItem.Reset();
                            } finally {
                                ssn.close();
                            }

                            break;
                        case rFailure:
                            logEntry(processItem, Table.Error.RSR.PeekFailure, getClass().getCanonicalName(), "processItems -> Read -> onPeek");
                            processItem.Errors.add(eRead);
                            processItem.Commands.add(cmdError);
                            processItem.Commands.add(cmdTeardown);
                            processItem.Commands.remove(cmdPoll);
                            break;
                    }
                    break;
                case Pending:
                    //processItem.Commands.remove(cmdRecv);
                    break;
                case Failure:
                    processItem.Errors.add(eReset);
                    processItem.Commands.add(cmdError);
                    processItem.Commands.add(cmdTeardown);
                    processItem.Commands.remove(cmdPoll);
                    processItem.Commands.remove(cmdRecv);
                    break;
            }
        } finally {
            Thread.setPriority(Settings.RSR.Items.ThreadPriorityNormal);
        }
    }
    private void processError(){
        processItem.Commands.remove(cmdError);
        processItem.Error();
    }
    private void processTeardown(){
        processItem.Commands.clear();
        if (processItem.keySelect!=null){
            processItem.keySelect.cancel();
            processItem.keySelect=null;
        }
        processItem.Buffers.Reset();
        processItem.Disconnected();
        processItem.Teardown();

        if (processItem.exceededTrys()) {
            processItem.Finalized();
            processItem.State = isFinalize;
            removalItems.add(processItem);

            processItem.Release();

        } else if (processItem.Kind==Client){
            // exceededTrys computed for persistent connections
            processItem.Commands.add(cmdConnect);
        }
        processItem.Errors.clear();
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
            if ( processItem.Errors.isEmpty() ){
                for (ItemCommand c:processItem.Commands) {
                    switch (c) {
                        case cmdAccept:
                            try {
                                processAccept();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processAccept", ex.toString());
                            }
                            break;
                        case cmdConnect:
                            try {
                                processConnect();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processConnect", ex.toString());
                            }
                            break;
                        case cmdSend:
                            try {
                                processSend();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processSend", ex.toString());
                            }
                            break;
                        case cmdRecv:
                            try {
                                processRecv();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processRecv", ex.toString());
                            }
                            break;
                        case cmdPoll:
                            try {
                                processPoll();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processPoll", ex.toString());
                            }
                            break;
                        case cmdError:
                            try {
                                processError();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processError", ex.toString());
                            }
                            break;
                        case cmdSetup:
                            try {
                                processSetup();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName(), "processSetup", ex.toString());
                            }
                            break;
                        case cmdTeardown:
                            try {
                                processTeardown();
                            } catch (Exception ex){
                                Syslog.Append(getClass().getCanonicalName() , "processTeardown", ex.getMessage());
                            }
                            break;
                    }
                }
            } else {
                processError();
                processTeardown();
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

