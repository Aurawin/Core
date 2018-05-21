package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.Buffers;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.time.Instant;

import static com.aurawin.core.rsr.def.ItemCommand.cmdSend;
import static com.aurawin.core.rsr.def.ItemKind.Client;
import static com.aurawin.core.rsr.def.ItemState.isNone;
import static java.net.StandardSocketOptions.TCP_NODELAY;

public class SocketHandlerPlain extends SocketHandler {
    @Override
    public void Teardown(){
    }

    @Override
    public void Setup(){
        try {
            Owner.Channel.configureBlocking(false);
        } catch (IOException ex) {
            Syslog.Append("SocketHandlerPlain", "Setup", ex.getMessage());
            Shutdown();
            return;
        }
        try {

        } catch (Exception ex){
            Syslog.Append("SocketHandlerPlain", "Setup", ex.getMessage());
            Shutdown();
            return;
        }
    }

    @Override
    public void beginHandshake() {}

    public SocketHandlerPlain(Item owner){
        super(owner);
    }

    @Override
    public boolean dataSendComplete(){
        return Owner.Buffers.Send.Size==0;
    }
    @Override
    public SocketHandlerResult Recv(){
        if (Owner.Channel.isConnected()==true) {
            Owner.Owner.BufferRead.clear();
            try {
                int i = Owner.Channel.read(Owner.Owner.BufferRead);
                if (i>0) {
                    Owner.renewTTL();
                    Owner.Owner.BufferRead.flip();
                    Owner.Buffers.Recv.write(Owner.Owner.BufferRead);
                    Owner.Owner.BufferRead.clear();
                } else if (i == -1 ) {
                    return SocketHandlerResult.Failure;
                }
            } catch (IOException ioe){
                return SocketHandlerResult.Failure;
            }
            return SocketHandlerResult.Complete;
        } else {
            return SocketHandlerResult.Failure;
        }
    }
    public SocketHandlerResult Send(){
        if (Owner.Buffers.Send.Size>0) {
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.read(Owner.Owner.BufferWrite);
            Owner.Owner.BufferWrite.flip();
            while (Owner.Owner.BufferWrite.hasRemaining()) {
                try {
                    Owner.renewTTL();
                    Owner.Channel.write(Owner.Owner.BufferWrite);
                } catch (IOException ioe){
                    return SocketHandlerResult.Failure;
                }
            }
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.sliceAtPosition();
            if (Owner.Buffers.Send.Size==0) Owner.Commands.remove(cmdSend);
        } else {
            if (Owner.Buffers.Send.Size == 0) Owner.Commands.remove(cmdSend);
        }
        return SocketHandlerResult.Complete;
    }


}
