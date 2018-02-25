package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.ItemState;
import com.aurawin.core.solution.Settings;

import javax.net.ssl.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import static com.aurawin.core.rsr.def.ItemError.eSSL;
import static com.aurawin.core.rsr.def.ItemKind.Client;
import static com.aurawin.core.rsr.def.ItemKind.Server;
import static com.aurawin.core.rsr.def.ItemState.isHandShake;
import static com.aurawin.core.rsr.def.ItemState.isNone;
import static com.aurawin.core.solution.Settings.RSR.Security.HandshakeTimeout;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;


public class SocketHandlerSecure extends SocketHandler {
    public SSLEngine Cryptor;
    private ByteBuffer peerAppData;
    private ByteBuffer peerNetData;
    private ByteBuffer localAppData;
    private ByteBuffer localNetData;

    private boolean issuedHandshake;
    private int iRead;
    private int iWrite;
    private SSLEngineResult CryptResult;
    private SSLEngineResult.HandshakeStatus handshakeStatus;
    private SSLEngineResult.Status Status;

    public SocketHandlerSecure(Item owner){
        super(owner);
        issuedHandshake=false;

        peerAppData = ByteBuffer.allocate(Settings.RSR.Server.SSLEnginePeerAppDataBuffer);
        peerNetData = ByteBuffer.allocate(Settings.RSR.Server.SSLEnginePeerNetDataBuffer);
        localAppData= ByteBuffer.allocate(Settings.RSR.Server.SSLEngineLocalAppDataBuffer);
        localNetData= ByteBuffer.allocate(Settings.RSR.Server.SSLEngineLocalNetDataBuffer);

    }


    @Override
    public void Release(){
        super.Release();

        Cryptor=null;
        peerAppData.clear();
        peerAppData=null;
        peerNetData.clear();
        peerNetData=null;
        localAppData.clear();
        localAppData=null;
        localNetData.clear();
        localNetData=null;
        CryptResult=null;
    }
    @Override
    public void Teardown(){
        try {
            Cryptor.closeInbound();
        } catch (SSLException e) {

        }

        Cryptor.closeOutbound();

        if (Key!=null){
            Key.cancel();
            Key=null;
        }
        if (Channel.isOpen()==true) {
            try{
                Channel.close();
            } catch (IOException ioe){
                // do nothing.  already closed.
            }
        }
        Owner.Disconnected();
        Owner.Finalized();
        Owner.State = isNone;

    }
    @Override
    public void Setup(){
        super.Setup();
        try {
            if (Owner.Kind==Client){
                //int p = Owner.Address.getPort();
                Cryptor=Owner.Owner.Security.Context.createSSLEngine(
                        Owner.Address.getAddress().getHostAddress(),
                        Owner.Address.getPort()
                );
                Cryptor.setUseClientMode(true);
            } else {
                Cryptor=Owner.Owner.Security.Context.createSSLEngine();
                Cryptor.setUseClientMode(false);
            }
            Cryptor.setNeedClientAuth(false);
            Cryptor.setWantClientAuth(false);
            Cryptor.setEnabledProtocols(Cryptor.getSupportedProtocols());
            Cryptor.setEnabledCipherSuites(Cryptor.getEnabledCipherSuites());
            Cryptor.setEnableSessionCreation(true);


            SSLParameters sslParams = Cryptor.getSSLParameters();
            sslParams.setNeedClientAuth(false);
            sslParams.setWantClientAuth(false);



            sslParams.setCipherSuites(Owner.Owner.Security.Context.getSupportedSSLParameters().getCipherSuites());
            sslParams.setProtocols(Owner.Owner.Security.Context.getSupportedSSLParameters().getProtocols());


            Cryptor.setSSLParameters(sslParams);

            Channel.socket().setKeepAlive(false);
            Channel.socket().setReuseAddress(false);
            Channel.socket().setReceiveBufferSize(Settings.RSR.SocketBufferRecvSize);
            Channel.socket().setSendBufferSize(Settings.RSR.SocketBufferSendSize);
            Channel.configureBlocking(false);

            beginHandshake();

        } catch (Exception e) {
            Syslog.Append("SocketHandlerSecure", "Setup", e.getMessage());
            Owner.Errors.add(eSSL);
            Owner.Error();
            Shutdown();
        }

    }

    public SocketHandlerResult Send() {
        try {
            if (Owner.Buffers.Send.Size>0) {
                localAppData.clear();
                Owner.Buffers.Send.read(localAppData);
                Owner.Buffers.Send.sliceAtPosition();
                localAppData.flip();
                localNetData.clear();
                while (localAppData.hasRemaining() ) {
                    CryptResult = Cryptor.wrap(localAppData,localNetData);
                    switch (CryptResult.getStatus()) {
                        case OK:
                            localNetData.flip();
                            while (localNetData.hasRemaining()) {
                                try {
                                    Channel.write(localNetData);
                                } catch (ClosedChannelException cce){
                                    Syslog.Append("SocketHandlerSecure", "Send.Channel.write", "Channel Closed");
                                    this.handshakeFailed();
                                } catch (IOException ioe){
                                    Syslog.Append("SocketHandlerSecure", "Send.Channel.write", "IO Channel Exception");
                                    this.handshakeFailed();
                                }
                            }

                            localNetData.clear();
                            break;
                        case CLOSED:
                            Shutdown();
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData.rewind(); // nothing to do wait for more data
                            break;
                        case BUFFER_OVERFLOW:
                            localNetData.compact();
                            localNetData.flip();
                            Channel.write(localNetData);
                            break;
                    }
                }
                localAppData.clear();
                if (Owner.Buffers.Send.Size==0) Owner.Owner.removeFromWriteQueue(Owner);
            } else {
                if (Owner.Buffers.Send.Size == 0) Owner.Owner.removeFromWriteQueue(Owner);
            }
            Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);
            return SocketHandlerResult.Complete;
        } catch (Exception e){
            Syslog.Append("SocketHandlerSecure", "Send", e.getMessage());
            return SocketHandlerResult.Failure;
        }
    }

    public SocketHandlerResult Recv() {
        try {
            iRead=Channel.read(peerNetData);
            if (iRead>0) {
                peerNetData.flip();
                while (peerNetData.hasRemaining()) {
                    CryptResult = Cryptor.unwrap(peerNetData, localAppData);
                    switch (CryptResult.getStatus()) {
                        case OK:
                            localAppData.flip();
                            Owner.Buffers.Recv.write(localAppData);
                            break;
                        case CLOSED:
                            Shutdown();
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData.rewind(); // nothing to do wait for more data
                            break;
                        case BUFFER_OVERFLOW:
                            peerNetData.compact();
                            localAppData.flip();
                            Owner.Buffers.Recv.write(localAppData);
                            break;
                    }
                    localAppData.clear();
                }
                peerNetData.compact();
                Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);

            } else if (iRead==-1){
                Shutdown();
                return SocketHandlerResult.Failure;
            }

        } catch (Exception e){
            Syslog.Append("SocketHandlerSecure", "Recv", e.getMessage());
            return SocketHandlerResult.Failure;
        }
        return SocketHandlerResult.Complete;
    }


    private void processHandshakeStep() throws IOException{
        handshakeStatus=Cryptor.getHandshakeStatus();
        switch (handshakeStatus) {
            case NEED_UNWRAP:
                handshakeUnwrap();
                break;
            case NEED_WRAP:
                handshakeWrap();
                break;
            case NEED_TASK:
                processNeedTask();
                break;
        }
    }
    @Override
    public void beginHandshake() throws IOException{
        if (issuedHandshake==false) {
            Owner.State = isHandShake;
            issuedHandshake = true;
            Cryptor.beginHandshake();
            handshakeStatus = Cryptor.getHandshakeStatus();

            Owner.TTL = Instant.now().plusMillis(HandshakeTimeout);

            while (
                    (
                            (handshakeStatus != FINISHED) &&
                            (handshakeStatus != NOT_HANDSHAKING)
                    ) &&
                            (
                                (Instant.now().isBefore(Owner.TTL)==true)
                            )

                    )
            {
                processHandshakeStep();
                handshakeStatus = Cryptor.getHandshakeStatus();
            }
            if (
                        (handshakeStatus == FINISHED) ||
                        (handshakeStatus == NOT_HANDSHAKING)
                    ){
                handshakeFinished();
            } else {
                handshakeFailed();
            }
        }
    }
    private void handshakeFailed(){
        this.localAppData.clear();
        this.localNetData.clear();
        this.peerAppData.clear();
        this.peerNetData.clear();

        handshakeStatus= FINISHED;
        Owner.State = ItemState.isNone;
        Owner.Errors.add(eSSL);
        Owner.Error();
        Owner.queueClose();

        Syslog.Append(
                "SocketHandlerSecure",
                "handshakeFailed",
                "Connecting to ["+Owner.Address.toString()+ "] failed due to a handshake failure."
        );

    }
    private void handshakeFinished() throws IOException {
        handshakeStatus= FINISHED;
        switch (Owner.Kind) {
            case Client :
                Key = Channel.register(Owner.Owner.Keys, SelectionKey.OP_WRITE | SelectionKey.OP_READ | SelectionKey.OP_CONNECT, Owner);
                break;
            case Server:
                Key = Channel.register(Owner.Owner.Keys, SelectionKey.OP_WRITE | SelectionKey.OP_READ, Owner);
                break;
        }
        Owner.State = ItemState.isEstablished;
        Owner.Connected();
    }
    private void handshakeWrap() throws IOException{
        localNetData.clear();
        CryptResult = Cryptor.wrap(localAppData, localNetData);
        handshakeStatus = CryptResult.getHandshakeStatus();
        Status = CryptResult.getStatus();
        switch (Status){
            case OK:
                localNetData.flip();
                while (localNetData.hasRemaining()) {
                    try {
                        Channel.write(localNetData);
                    } catch (ClosedChannelException cce){
                        Syslog.Append("SocketHandlerSecure", "handshakeWrap.Channel.write", "Channel Closed");
                        this.handshakeFailed();
                    } catch (IOException cce){
                        Syslog.Append("SocketHandlerSecure", "handshakeWrap.Channel.write", "IO Channel Exception");
                        this.handshakeFailed();
                    }
                }
                localNetData.clear();
                break;
            case CLOSED:
                handshakeFailed();
                break;
        }

    }
    private void handshakeUnwrap() throws IOException {
        iRead=Channel.read(peerNetData);
        if (iRead>0){
            peerNetData.flip();
            CryptResult = Cryptor.unwrap(peerNetData, peerAppData);
            handshakeStatus = CryptResult.getHandshakeStatus();
            Status = CryptResult.getStatus();
            peerNetData.compact();
        }
    }
    private void processUnwrap() throws IOException{
        iRead = Channel.read(peerNetData);
        if (iRead>0) {
            peerNetData.flip();
            CryptResult = Cryptor.unwrap(peerNetData, peerAppData);
            switch (CryptResult.getStatus()) {
                case OK:
                    peerAppData.flip();
                    Owner.Buffers.Recv.write(peerAppData);
                    break;
                case CLOSED:
                    Shutdown();
                    break;
                case BUFFER_UNDERFLOW:
                    peerNetData.rewind();
                    return;
                case BUFFER_OVERFLOW:
                    // too much data in peerAppData...
                    // write and try again?
                    iRead = 0;// todo
                    break;
            }
        }

    }

    private void processNeedTask() throws IOException{
        Runnable task;
        while ((task=Cryptor.getDelegatedTask()) != null) {
            task.run();
        }
    }




}
