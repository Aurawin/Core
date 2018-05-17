package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
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
        if (Key!=null){
            Key.cancel();
            Key=null;
        }
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
            switch (Owner.Kind) {
                case Client :
                    Key = Owner.Channel.register(Owner.Owner.Keys, SelectionKey.OP_WRITE | SelectionKey.OP_READ | SelectionKey.OP_CONNECT, Owner);
                    break;
                case Server:
                    Key = Owner.Channel.register(Owner.Owner.Keys, SelectionKey.OP_WRITE | SelectionKey.OP_READ, Owner);
                    break;
            }
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
    public SocketHandlerResult Recv(){
        if (Owner.Channel.isConnected()==true) {
            Owner.Owner.BufferRead.clear();
            try {
                int i = Owner.Channel.read(Owner.Owner.BufferRead);
                if (i == -1 )
                    return SocketHandlerResult.Failure;
            } catch (IOException ioe){
                return SocketHandlerResult.Failure;
            }
            Owner.Owner.BufferRead.flip();
            Owner.Buffers.Recv.write(Owner.Owner.BufferRead);
            Owner.Owner.BufferRead.clear();

            Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);

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
