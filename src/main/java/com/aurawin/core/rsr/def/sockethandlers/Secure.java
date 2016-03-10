package com.aurawin.core.rsr.def.sockethandlers;

import com.aurawin.core.rsr.Item;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Secure extends Handler {
    public SSLSocketFactory Factory;
    public SSLSocket Socket;
    @Override
    public HandlerResult Teardown(){
        return HandlerResult.Failure;
    }
    @Override
    public HandlerResult Setup(boolean Accepted){
        try {
            Socket=(SSLSocket) Factory.createSocket(
                    Channel.socket(),
                    Channel.getLocalAddress().toString(),
                    Channel.socket().getLocalPort(),
                    true
            );
            Socket.setUseClientMode(!Accepted);
            Socket.startHandshake();
            Socket.setKeepAlive(true);
            return HandlerResult.Complete;
        } catch (Exception e){
            return HandlerResult.Failure;
        }
    }

    @Override
    public HandlerResult Send() {
        return HandlerResult.Failure;
    }

    @Override
    public HandlerResult Recv() {
        return HandlerResult.Failure;
    }

    public Secure(Item owner){
        super(owner);
        Factory = owner.Owner.Security.Context.getSocketFactory();
    }
}
