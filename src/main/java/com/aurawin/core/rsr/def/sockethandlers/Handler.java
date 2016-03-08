package com.aurawin.core.rsr.def.sockethandlers;

import com.aurawin.core.rsr.Item;

public abstract class Handler implements Methods {
    public Handler(Item owner) {
        Owner = owner;
    }
    protected Item Owner;
}
