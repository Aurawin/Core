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
    protected Item Owner;
    public SocketChannel Channel;
    public SelectionKey Key;

    public void Setup() {
        try {
            Channel.setOption(TCP_NODELAY,true);
            Channel.socket().setSoLinger(false,0);
            Channel.socket().setReceiveBufferSize(Settings.RSR.SocketBufferRecvSize);
            Channel.socket().setSendBufferSize(Settings.RSR.SocketBufferSendSize);


        } catch (IOException ex){
            Syslog.Append("SocketHandler", "Setup", ex.getMessage());
        }
    }
    public void Shutdown(){
        Owner.Owner.scheduleRemoval(Owner);
    }
    public void Release(){
        try {
            if (Channel!=null)  Channel.close();
            if (Key!=null) Key.cancel();
        } catch (Exception ex){
            Syslog.Append("SocketHandler", "Release", ex.getMessage());
        } finally{
            Owner=null;
            Channel=null;
            Key=null;
        }
    }
}
