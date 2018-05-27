package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;
import java.io.IOException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static java.net.SocketOptions.SO_LINGER;
import static java.net.StandardSocketOptions.TCP_NODELAY;

public abstract class SocketHandler implements SocketMethods {
    public SocketHandler(Item owner) {
        Owner = owner;
    }
    protected long bytesToSend;
    protected long bytesToRecv;

    protected Item Owner;
    protected boolean issuedHandshake = false;
    public void Setup() {
        try {
            bytesToRecv=0;
            bytesToSend=0;
            issuedHandshake=false;
            Owner.Channel.setOption(TCP_NODELAY,true);
            Owner.Channel.socket().setSoLinger(true,2000);
            Owner.Channel.socket().setReceiveBufferSize(Settings.RSR.SocketBufferRecvSize);
            Owner.Channel.socket().setSendBufferSize(Settings.RSR.SocketBufferSendSize);


        } catch (IOException ex){
            Syslog.Append("SocketHandler", "Setup", ex.getMessage());
        }
    }

    public boolean hasBytesToSend(){
        return ( (Owner.Buffers.Send.position()!=Owner.Buffers.Send.size()) || (bytesToSend>0) );
    }
    public void Shutdown(){ }
    public void Release(){
       Owner=null;
    }
}
