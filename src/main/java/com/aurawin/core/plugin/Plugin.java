package com.aurawin.core.plugin;

public abstract class Plugin implements Method {
    Method [] Methods;
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    protected long ID;

}
