package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.time.Instant;

import static com.aurawin.core.rsr.def.ItemState.isNone;
import static java.net.StandardSocketOptions.TCP_NODELAY;

public class SocketHandlerPlain extends SocketHandler {
    @Override
    public void Teardown(){
        if (Key!=null){
            Key.cancel();
            Key=null;
        }
        if (Channel.isOpen()==true) {
            try {
                Channel.close();
            } catch (IOException ioe) {
                // do nothing.  already closed.
            }
        }
        Owner.Disconnected();
        Owner.Finalized();
        Owner.State = isNone;
    }

    @Override
    public void Setup(boolean accepted){
        try {
            Channel.configureBlocking(false);
        } catch (IOException ioe) {
            Shutdown();
            return;
        }
        try {
            Key = Channel.register(Owner.Owner.rwSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, Owner);
        } catch (IllegalBlockingModeException ibme){
            Shutdown();
            return;
        } catch (ClosedChannelException cce) {
            Shutdown();
            return;
        }
    }

    public SocketHandlerPlain(Item owner){
        super(owner);
    }

    @Override
    public SocketHandlerResult Recv(){
        if (Channel.isConnected()==true) {
            Owner.Owner.BufferRead.clear();
            try {
                int i = Channel.read(Owner.Owner.BufferRead);
                if (i <= 0 )
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
                    Channel.write(Owner.Owner.BufferWrite);

                } catch (IOException ioe){
                    return SocketHandlerResult.Failure;
                }
            }
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.sliceAtPosition();
            if (Owner.Buffers.Send.Size==0) Owner.Owner.removeFromWriteQueue(Owner);
        } else {
            if (Owner.Buffers.Send.Size == 0) Owner.Owner.removeFromWriteQueue(Owner);
        }
        return SocketHandlerResult.Complete;
    }

}
