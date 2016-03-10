package com.aurawin.core.rsr.def.sockethandlers;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import java.util.Date;

import static com.aurawin.core.rsr.def.ItemState.isFinalize;
import static com.aurawin.core.rsr.def.ItemState.isNone;
import static com.aurawin.core.rsr.def.rsrResult.rSuccess;

public class Plain extends Handler {
    @Override
    public HandlerResult Teardown(){
        if (Channel.isConnected()==true) {
            try{
                Channel.close();
            }catch (IOException ioe){
                // do nothing.  already closed.
            }
            if (Owner.onDisconnected() == rSuccess) {
                Owner.State = isFinalize;
            } else {
                return HandlerResult.Failure;
            }
            if (Owner.onFinalize() == rSuccess) {
                Owner.State = isNone;
            } else {
                return HandlerResult.Failure;
            }
        }
        return HandlerResult.Complete;
    }

    @Override
    public HandlerResult Setup(boolean Accepted){
        try {
            Channel.configureBlocking(false);
        } catch (IOException e){
            return HandlerResult.Failure;
        }
        return HandlerResult.Complete;
    }

    public Plain(Item owner){
        super(owner);
    }

    @Override
    public HandlerResult Recv(){
        if (Channel.isConnected()==true) {
            Owner.Owner.BufferRead.clear();
            try {
                Channel.read(Owner.Owner.BufferRead);
            } catch (IOException ioe){
                return HandlerResult.Failure;
            }
            Owner.Owner.BufferRead.flip();
            Owner.Buffers.Recv.write(Owner.Owner.BufferRead);
            Owner.Owner.BufferRead.clear();

            Owner.TTL = Instant.now().plusMillis(Settings.RSR.Server.Timeout);

            return HandlerResult.Complete;
        } else {
            return HandlerResult.Failure;
        }
    }
    public HandlerResult Send(){
        if (Owner.Buffers.Send.Size>0) {
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.read(Owner.Owner.BufferWrite);
            Owner.Owner.BufferWrite.flip();
            while (Owner.Owner.BufferWrite.hasRemaining()) {
                try {
                    Channel.write(Owner.Owner.BufferWrite);
                } catch (IOException ioe){
                    return HandlerResult.Failure;
                }
            }
            Owner.Owner.BufferWrite.clear();
            Owner.Buffers.Send.sliceAtPosition();
            if (Owner.Buffers.Send.Size==0) Owner.Owner.removeFromWriteQueue(Owner);
        } else {
            if (Owner.Buffers.Send.Size == 0) Owner.Owner.removeFromWriteQueue(Owner);
        }
        return HandlerResult.Complete;
    }

}
