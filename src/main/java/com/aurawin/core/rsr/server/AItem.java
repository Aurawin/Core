package com.aurawin.core.rsr.server;

public abstract class AItem {
    protected abstract int onDataReceived();
    protected abstract int onDisconnected();
    protected abstract int onAccepted();
    protected abstract int onRejected();
    protected abstract int onError();

}
