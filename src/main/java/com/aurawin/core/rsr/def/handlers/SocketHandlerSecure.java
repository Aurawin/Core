package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.ItemCommand;
import com.aurawin.core.rsr.def.ItemState;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;
import com.sun.net.httpserver.Authenticator;

import javax.net.ssl.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Instant;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.xml.transform.OutputKeys;

import static com.aurawin.core.rsr.def.ItemCommand.cmdError;
import static com.aurawin.core.rsr.def.ItemCommand.cmdTeardown;
import static com.aurawin.core.rsr.def.ItemError.*;
import static com.aurawin.core.rsr.def.ItemKind.Client;
import static com.aurawin.core.rsr.def.ItemKind.Server;
import static com.aurawin.core.rsr.def.ItemState.isHandShake;
import static com.aurawin.core.rsr.def.ItemState.isNone;
import static com.aurawin.core.rsr.def.handlers.SocketHandlerResult.Complete;
import static com.aurawin.core.rsr.def.handlers.SocketHandlerResult.Failure;
import static com.aurawin.core.rsr.def.handlers.SocketHandlerResult.Pending;
import static com.aurawin.core.solution.Settings.RSR.ByteBufferLarger;
import static com.aurawin.core.solution.Settings.RSR.Security.*;
import static com.aurawin.core.solution.Settings.RSR.SocketBufferRecvSize;
import static com.aurawin.core.solution.Settings.RSR.SocketBufferSendSize;
import static com.aurawin.core.solution.Settings.Security.Ciphers;
import static com.aurawin.core.solution.Settings.Security.Protocols;
import static java.time.LocalTime.now;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.*;
import static javax.net.ssl.SSLEngineResult.Status.*;


public class SocketHandlerSecure extends SocketHandler {
    public SSLEngine Cryptor;
    public SSLContext Context;

    private boolean needNetInFlip = true;


    private int iRead;
    private int iWrite;
    private long bytesRead;
    private long bytesWrite;

    private SSLEngineResult CryptResult;
    private SSLEngineResult.HandshakeStatus handshakeStatus;
    private SSLEngineResult.Status Status;

    ByteBuffer bbNetOut = ByteBuffer.allocateDirect(Settings.RSR.ByteBufferLarger);
    ByteBuffer bbAppOut = ByteBuffer.allocateDirect(Settings.RSR.ByteBufferRead);
    ByteBuffer bbAppIn = ByteBuffer.allocateDirect(Settings.RSR.ByteBufferLarger);
    ByteBuffer bbNetIn = ByteBuffer.allocateDirect(Settings.RSR.ByteBufferSmaller);


    public SocketHandlerSecure(Item owner){
        super(owner);
        issuedHandshake=false;
    }

    @Override
    public boolean dataSendComplete(){

        return (Owner.Buffers.Send.Size==0) && !Bytes.Buffer.containsData(bbNetOut) && !Bytes.Buffer.containsData(bbAppOut);

    }

    @Override
    public void Release(){
        super.Release();
        bbAppOut=null;
        bbAppIn=null;
        bbNetOut=null;
        bbNetIn=null;
        Cryptor=null;
        CryptResult=null;
    }
    @Override
    public void Teardown(){
        if (Cryptor!=null){
            Cryptor.closeOutbound();

            try {
                Cryptor.closeInbound();

            } catch (SSLException e1) {

            }
        }

    }
    @Override
    public void Setup(){
        super.Setup();
        try {
            bytesToSend=0;
            bytesToRecv=0;
            issuedHandshake=false;
            Context = Owner.Owner.Security.getContext();

            Cryptor=Context.createSSLEngine();

            needNetInFlip =  (Owner.Kind==Server);

            Cryptor.setUseClientMode((Owner.Kind==Client));

            Cryptor.setNeedClientAuth(false);
            Cryptor.setWantClientAuth(false);

            Cryptor.setEnabledProtocols(Context.getSupportedSSLParameters().getProtocols());//Settings.Security.Protocols
            Cryptor.setEnabledCipherSuites(Context.getSupportedSSLParameters().getCipherSuites());//Ciphers
            Cryptor.setEnableSessionCreation(true);

            Owner.Channel.socket().setSendBufferSize(SocketBufferSendSize);
            Owner.Channel.socket().setReceiveBufferSize(SocketBufferRecvSize);
            Owner.Channel.socket().setTcpNoDelay(true);
            Owner.Channel.configureBlocking(false);

            beginHandshake();

        } catch (Exception e) {
            Syslog.Append("SocketHandlerSecure", "Setup", e.toString());
            Owner.Errors.add(eSSL);
            Owner.Error();
            Shutdown();
        }

    }
    private HandshakeStatus handshakeWrap() {
        try {
            bbAppOut.flip();
            bbAppOut.limit(bbAppOut.capacity());

            bbNetOut.limit(bbNetOut.capacity());
            CryptResult = Cryptor.wrap(bbAppOut,bbNetOut);
            bbAppOut.compact();


            handshakeStatus = CryptResult.getHandshakeStatus();
            Status = CryptResult.getStatus();
            switch (Status) {
                case OK:
                    bbNetOut.flip();
                    while (bbNetOut.hasRemaining()) {
                        iWrite = Owner.Channel.write(bbNetOut);
                        bytesWrite+=iWrite;
                    }
                    bbNetOut.compact();
                    bbNetOut.flip();
                    return handshakeStatus;
                case BUFFER_UNDERFLOW:
                    Syslog.Append("SocketHandlerSecure", "handshakeWrap", "BUFFER_UNDERFLOW unexpected.");
                    return NOT_HANDSHAKING;
                case BUFFER_OVERFLOW:
                    ByteBuffer bb = ByteBuffer.allocate(bbNetOut.capacity()+Settings.RSR.ByteBufferIncreaseBy);
                    bb.put(bbNetOut);
                    bbNetOut=bb;
                    return NOT_HANDSHAKING;
                case CLOSED:
                    return NOT_HANDSHAKING;
            }

        } catch (SSLException sle){
            Syslog.Append("SocketHandlerSecure", "handshakeWrap.Channel.wrap", sle.toString());
            handshakeFailed();
            return NOT_HANDSHAKING;
        } catch (IOException ioe){
            Syslog.Append("SocketHandlerSecure", "handshakeWrap.Channel.wrap", ioe.getMessage());
            handshakeFailed();
            return NOT_HANDSHAKING;
        }

        return NOT_HANDSHAKING;
    }
    private HandshakeStatus handshakeUnwrap()    {
        // todo timeout check here (infinite loop)
        boolean needTry = true;
        iRead=0;
        try {
            while ( (iRead!=-1) && (needTry) && (Instant.now().isBefore(Owner.TTL) && Owner.Errors.isEmpty()) ){
                if (needNetInFlip) bbNetIn.flip();
                needNetInFlip=false;
                needTry=false;
                CryptResult = Cryptor.unwrap(bbNetIn, bbAppIn);

                Status = CryptResult.getStatus();
                handshakeStatus = Cryptor.getHandshakeStatus();
                switch (Status) {
                    case OK:
                        bbNetIn.compact();
                        bbNetIn.flip();
                        return handshakeStatus;
                    case CLOSED:
                        Shutdown();
                        return handshakeStatus;
                    case BUFFER_UNDERFLOW:
                        //bbNetIn.compact();
                        bbNetIn.limit(bbNetIn.capacity());
                        iRead = Owner.Channel.read(bbNetIn);
                        if (iRead==-1){
                            Owner.Errors.add(eRead);
                            Owner.Errors.add(eSSL);
                        } else if (iRead>=0) {
                            bytesRead+=iRead;
                            bbNetIn.flip();
                        }
                        needTry = true;
                        // wait for more data.
                        break;
                    case BUFFER_OVERFLOW:
                        needTry=true;
                        break;

                }
            }
        } catch (SSLException se){
            Syslog.Append("SocketHandlerSecure", "handshakeUnwrap.Cryptor.unwrap", se.getMessage());
            handshakeFailed();
            return NOT_HANDSHAKING;
        } catch (IOException ioe){
            Syslog.Append("SocketHandlerSecure", "handshakeUnwrap.Cryptor.unwrap", ioe.getMessage());
            handshakeFailed();
            return NOT_HANDSHAKING;
        }

        return CryptResult.getHandshakeStatus();

    }


    @Override
    public void beginHandshake() throws IOException  {
        if (issuedHandshake==false) {

            Owner.State = isHandShake;
            issuedHandshake = true;
            needNetInFlip=true;
            try {
                Owner.TTL = Instant.now().plusMillis(HandshakeTimeoutDebug);
                Cryptor.beginHandshake();
                handshakeStatus = Cryptor.getHandshakeStatus();
                while (
                        handshakeStatus!=FINISHED &&
                                handshakeStatus!=NOT_HANDSHAKING &&
                                Owner.Errors.isEmpty()
                        ) {
                    if (Instant.now().isAfter(Owner.TTL)) {
                        Owner.Errors.add(eTimeout);
                        Owner.Errors.add(eSSL);
                        Owner.Error();
                    }
                    switch (handshakeStatus) {
                        case NEED_UNWRAP:
                            handshakeStatus=handshakeUnwrap();
                            break;
                        case NEED_WRAP:
                            handshakeStatus=handshakeWrap();
                            break;
                        case NEED_TASK:
                            handshakeStatus=processNeedTask();
                            break;
                    }
                }
                if ( (((handshakeStatus == NOT_HANDSHAKING)) || (handshakeStatus == FINISHED)) && (Owner.Errors.isEmpty()) ) {
                    handshakeFinished();
                }
            } catch (SSLException sle) {
                Syslog.Append("SocketHandlerSecure", "beginHandshake", "SSL Exception.");
                handshakeFailed();
            }
        }
    }

    public SocketHandlerResult Send() {
        boolean hasData=false;
        if ( Owner.Buffers.Send.hasRemaining())   {
            bbAppOut.compact();
            if (bbAppOut.position()==bbAppOut.limit())
              bbAppOut.flip();
            Owner.Buffers.Send.read(bbAppOut);
            bbAppOut.flip();
            hasData=true;
        }
        try {
            iWrite = -1;
            if (bytesToSend>0 || hasData) {
                while ((iWrite != 0) && ((bbAppOut.hasRemaining()) || (bbNetOut.hasRemaining()))) {
                    iWrite = -1;
                    while (bbAppOut.hasRemaining() || bbNetOut.hasRemaining() && (iWrite == -1)) {
                        CryptResult = Cryptor.wrap(bbAppOut, bbNetOut);
                        Status = CryptResult.getStatus();
                        switch (Status) {
                            case BUFFER_UNDERFLOW:
                                iWrite = 0;
                                return Pending;
                            case BUFFER_OVERFLOW:
                                // send existing data then get more data
                                iWrite = 0;
                                break;
                            case CLOSED:
                                iWrite = 0;
                                Owner.Errors.add(eSSL);
                                Owner.Errors.add(eWrite);
                                Owner.Errors.add(eReset);
                                return Failure;
                            case OK:
                                bytesToSend+=bbNetOut.limit()-bbNetOut.position();
                        }
                    }
                    bbAppOut.compact();
                    bbAppOut.flip();
                    bbNetOut.flip();
                    iWrite = -1;
                    if (bbNetOut.hasRemaining()) {
                        while ((iWrite != 0) && (bbNetOut.hasRemaining())) {
                            Owner.renewTTL();
                            iWrite = Owner.Channel.write(bbNetOut);
                            bytesWrite += iWrite;
                            bytesToSend-=iWrite;
                        }
                        bbNetOut.compact();
                        return Complete;
                    } else {
                        bbNetOut.compact();
                        return Pending;
                    }

                }
                return Complete;
            } else {
                return Complete;
            }

        } catch (SSLException sle){
            Syslog.Append("SocketHandlerSecure", "Send.Cryptor.wrap", "SSL Exception");
            cryptorFailed();
            return Failure;
        } catch (IOException ioe){
            Syslog.Append("SocketHandlerSecure", "Send.Cryptor.wrap", ioe.getMessage());
            cryptorFailed();
            return Failure;
        }

    }

    public SocketHandlerResult Recv() {
        try {
            iRead = Owner.Channel.read(bbNetIn);
            if (iRead>0) {
                bytesRead += iRead;
                Owner.renewTTL();
                bbNetIn.flip();
                while (bbNetIn.hasRemaining() && Owner.Errors.isEmpty()) {
                    CryptResult = Cryptor.unwrap(bbNetIn, bbAppIn);
                    Status = CryptResult.getStatus();
                    switch (Status) {
                        case OK:
                            bbAppIn.flip();

                            while (bbAppIn.hasRemaining()) {
                                Owner.Buffers.Recv.write(bbAppIn);
                            }

                            bbAppIn.clear();

                            break;
                        case CLOSED:
                            Owner.Errors.add(eRead);
                            Owner.Errors.add(eSSL);
                            return Failure;
                        case BUFFER_UNDERFLOW:
                            bbNetIn.compact();
                            return Pending;
                        case BUFFER_OVERFLOW:
                            // wait to read more data
                            bbNetIn.compact();
                            return Pending;

                    }
                }
                bbNetIn.compact();
            } else if (iRead==0){
                return Complete;

            } else if (iRead == -1) {
                Owner.Errors.add(eSSL);
                Owner.Errors.add(eRead);
                Owner.Errors.add(eReset);
                return Failure;
            }


        } catch (Exception e){
            Syslog.Append("SocketHandlerSecure", "Recv", e.getMessage());
            return SocketHandlerResult.Failure;
        }

        return Complete;
    }
    private void cryptorFailed() {
        Owner.State = ItemState.isNone;
        Owner.Errors.add(eSSL);
    }
    private void handshakeFailed(){
        handshakeStatus=NOT_HANDSHAKING;
        Owner.State = ItemState.isNone;
        Owner.Errors.add(eSSL);
        Syslog.Append(
                "SocketHandlerSecure",
                "handshakeFailed",
                "Connection ["+Owner.Address.toString()+ "] failed due to a handshake failure."
        );

    }
    private void handshakeFinished() {
        bbNetIn.clear();
        bbNetOut.clear();

        bbAppOut.clear();
        bbAppIn.clear();

        handshakeStatus= FINISHED;
        try {

            Owner.Channel.configureBlocking(false);
            Owner.State = ItemState.isEstablished;

        } catch (CancelledKeyException cke){
            handshakeFailed();
            Syslog.Append("SocketHandlerSecure", "handshakeFinished.Channel.register", cke.toString());

        } catch (IOException ioe){
            handshakeFailed();
        }

    }



    private HandshakeStatus processNeedTask() {
        Runnable task;
        while ((task=Cryptor.getDelegatedTask()) != null) {
            task.run();
        }
        return Cryptor.getHandshakeStatus();

    }

}
