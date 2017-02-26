package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import static com.aurawin.core.rsr.def.ItemState.isNone;


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
    private SocketSendReceiveCallback Send;
    private SocketSendReceiveCallback Recv;
    private SocketSendReceiveCallback SendPreHandshake;
    private SocketSendReceiveCallback SendPostHandshake;
    private SocketSendReceiveCallback RecvPreHandshake;
    private SocketSendReceiveCallback RecvPostHandshake;

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
        Send=null;
        Recv=null;
        SendPreHandshake=null;
        SendPostHandshake=null;
        RecvPreHandshake=null;
        RecvPostHandshake=null;

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
    public void Setup(boolean accepted){
        super.Setup(accepted);
        try {
            Cryptor=Owner.Owner.Security.Context.createSSLEngine();
            Cryptor.setUseClientMode(!accepted);
            Cryptor.setNeedClientAuth(false);
            Cryptor.setEnableSessionCreation(true);
            SSLParameters sslParams = Cryptor.getSSLParameters();
            sslParams.setCipherSuites(Owner.Owner.Security.Context.getSupportedSSLParameters().getCipherSuites());
            sslParams.setProtocols(Owner.Owner.Security.Context.getSupportedSSLParameters().getProtocols());
            Cryptor.setSSLParameters(sslParams);

            Channel.socket().setKeepAlive(false);
            Channel.socket().setReuseAddress(false);
            Channel.socket().setReceiveBufferSize(Settings.RSR.SocketBufferRecvSize);
            Channel.socket().setSendBufferSize(Settings.RSR.SocketBufferSendSize);
            Channel.configureBlocking(false);

            Key = Channel.register(Owner.Owner.rwSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, Owner);

            beginHandshake();


        } catch (Exception e) {

        }

    }
    @Override
    public SocketHandlerResult Send() {
        return Send.Perform();
    }
    @Override
    public SocketHandlerResult Recv() {
        return Recv.Perform();
    }
    private void processHandshakeStep() throws IOException{
        handshakeStatus=Cryptor.getHandshakeStatus();
        switch (handshakeStatus) {
            case FINISHED:
                Send = SendPostHandshake;
                Recv = RecvPostHandshake;
            case NEED_UNWRAP:
                processHandshakeUnwrap();
                break;
            case NEED_WRAP:
                handshakeNeedWrap();
                break;
            case NEED_TASK:
                processNeedTask();
                break;
        }
        Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);
    }
    private void beginHandshake() throws IOException{
        if (issuedHandshake==false) {
            issuedHandshake = true;
            Cryptor.beginHandshake();
        }
    }
    private void handshakeFinished(){
        handshakeStatus=HandshakeStatus.FINISHED;
        Send=SendPostHandshake;
        Recv=RecvPostHandshake;
    }
    private void processHandshakeUnwrap() throws IOException {
        CryptResult = Cryptor.unwrap(peerNetData, peerAppData);
        switch (CryptResult.getStatus()) {
            case OK:
                peerNetData.compact();
                peerNetData.flip();

                iRead=0;
                //peerNetData.flip();
                //peerNetData.compact();
                break;
            case CLOSED:
                Shutdown();
                break;
            case BUFFER_UNDERFLOW:
                peerNetData.rewind();
                iRead=0;
                return;
            case BUFFER_OVERFLOW:
                iRead=0;
                break;
        }
        switch (CryptResult.getHandshakeStatus()) {
            case FINISHED :
                handshakeFinished();
                break;
            case NEED_WRAP :
                handshakeNeedWrap();
                break;
            case NEED_UNWRAP :
                processHandshakeUnwrap();
                break;
            case NEED_TASK :
                processNeedTask();
                break;
            case NOT_HANDSHAKING:
                handshakeFinished();
                break;
        }
    }
    private void processUnwrap() throws IOException{
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
                iRead=0;// todo
                break;
        }
    }

    private void handshakeNeedWrap() throws SSLException,IOException{
        // Generate handshaking data
        CryptResult = Cryptor.wrap(localAppData, localNetData);
        handshakeStatus=CryptResult.getHandshakeStatus();
        switch (CryptResult.getStatus()) {
            case OK:
                localNetData.flip();
                while (localNetData.hasRemaining()) {
                    iWrite=Channel.write(localNetData);
                    if (iWrite<= 0) {
                        // Handle closed channel
                        Shutdown();
                    }

                }
                localNetData.clear();
                break;
            case CLOSED:
                Shutdown();
                break;
            case BUFFER_UNDERFLOW:
                iWrite=0;
                break;
            case BUFFER_OVERFLOW:
                iWrite=0;
                break;
        }
        switch (CryptResult.getHandshakeStatus()) {
            case FINISHED :
                handshakeFinished();
                break;
            case NEED_WRAP :
                handshakeNeedWrap();
                break;
            case NEED_UNWRAP :
                //processHandshakeUnwrap();
                break;
            case NEED_TASK :
                processNeedTask();
                break;
            case NOT_HANDSHAKING:
                handshakeFinished();
                break;
        }
    }
    private void processNeedTask() throws IOException{
        Runnable task;
        while ((task=Cryptor.getDelegatedTask()) != null) {
            task.run();
        }
        processHandshakeStep();
    }

    private void processRecv() throws IOException{
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
    }
    private void processSend() throws IOException{
        if (Owner.Buffers.Send.Size>0) {
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.read(Owner.Owner.BufferWrite);
            Owner.Owner.BufferWrite.flip();
            while (Owner.Owner.BufferWrite.hasRemaining() ) {
                CryptResult = Cryptor.wrap(Owner.Owner.BufferWrite,localNetData);
                switch (CryptResult.getStatus()) {
                    case OK:
                        localNetData.flip();
                        while (localNetData.hasRemaining())
                            Channel.write(localNetData);
                        localNetData.clear();
                        break;
                    case CLOSED:
                        Shutdown();
                        break;
                    case BUFFER_UNDERFLOW:
                        peerNetData.rewind(); // nothing to do wait for more data
                        break;
                    case BUFFER_OVERFLOW:
                        peerNetData.compact();
                        localNetData.flip();
                        Channel.write(localNetData);
                        break;
                }
            }
            Owner.Buffers.Send.sliceAtPosition();
            if (Owner.Buffers.Send.Size==0) Owner.Owner.removeFromWriteQueue(Owner);
        } else {
            if (Owner.Buffers.Send.Size == 0) Owner.Owner.removeFromWriteQueue(Owner);
        }
    }
    public SocketHandlerSecure(Item owner){
        super(owner);
        issuedHandshake=false;

        peerAppData = ByteBuffer.allocate(Settings.RSR.Server.SSLEnginePeerAppDataBuffer);
        peerNetData = ByteBuffer.allocate(Settings.RSR.Server.SSLEnginePeerNetDataBuffer);
        localAppData= ByteBuffer.allocate(Settings.RSR.Server.SSLEngineLocalAppDataBuffer);
        localNetData= ByteBuffer.allocate(Settings.RSR.Server.SSLEngineLocalNetDataBuffer);

        SendPreHandshake=new SocketSendReceiveCallback() {
            @Override
            public SocketHandlerResult Perform() {
                return SocketHandlerResult.Pending;
            }
        };
        RecvPreHandshake=new SocketSendReceiveCallback() {
            @Override
            public SocketHandlerResult Perform() {
                try {
                    peerNetData.compact();
                    iRead=Channel.read(peerNetData);
                    if (iRead>0) {
                        peerNetData.flip();
                        processHandshakeStep();
                    } else if (iRead==-1){
                        Shutdown();
                    }

                } catch (IOException e){
                    iRead=0;
                } finally{
                    if (handshakeStatus==HandshakeStatus.FINISHED) {
                        peerAppData.clear();
                        localAppData.clear();
                        localNetData.clear();
                        if (peerNetData.hasRemaining()) {
                            try {
                                processRecv();
                            } catch (IOException e) {
                                return SocketHandlerResult.Failure;
                            }
                        }
                        peerNetData.clear();
                    }
                    return SocketHandlerResult.Pending;
                }


            }
        };
        SendPostHandshake=new SocketSendReceiveCallback() {
            @Override
            public SocketHandlerResult Perform() {
                try {
                    processSend();
                    Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);
                    return SocketHandlerResult.Complete;
                } catch (Exception e){
                    return SocketHandlerResult.Failure;
                }
            }
        };
        RecvPostHandshake=new SocketSendReceiveCallback() {
            @Override
            public SocketHandlerResult Perform() {
                try {
                        iRead=Channel.read(peerNetData);
                        if (iRead>0) {
                            peerNetData.flip();
                            processRecv();
                        } else if (iRead==-1){
                            Shutdown();
                            return SocketHandlerResult.Failure;
                        }

                } catch (Exception e){
                    return SocketHandlerResult.Failure;
                }
                return SocketHandlerResult.Complete;
            }
        };
        Send=SendPreHandshake;
        Recv=RecvPreHandshake;
    }


}
