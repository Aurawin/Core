package com.aurawin.core.rsr.def.sockethandlers;

import com.aurawin.core.rsr.Item;

public class Secure extends Handler {
    @Override
    public HandlerResult Teardown(){
        return HandlerResult.Failure;
    }
    @Override
    public HandlerResult Setup(){
        return HandlerResult.Failure;
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

    }
}
