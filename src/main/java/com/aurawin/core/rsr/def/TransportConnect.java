package com.aurawin.core.rsr.def;

import com.aurawin.core.rsr.Item;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.time.Instant;

public class TransportConnect {
    private int Trys;
    private TransportConnectStatus Status;
    private Item Owner;
    private Object transportObject;
    private Method transportConstructor;
    private InetSocketAddress transportAddress;
    private Instant TTL;

    public TransportConnect(Object transportObject, Method transportConstructor, InetSocketAddress transportAddress) {
        this.transportObject = transportObject;
        this.transportConstructor = transportConstructor;
        this.transportAddress= transportAddress;
        this.TTL = Instant.now();
        this.Trys = 0;
    }

    public Object getObject() {
        return transportObject;
    }

    public Method getMethod() {
        return transportConstructor;
    }

    public void setOwner(Item owner){
        Owner = owner;
    }

    public void setTTL(Instant ttl){ TTL= ttl;  }
    public Instant getTTL(){ return TTL;  }

    public TransportConnectStatus getStatus() {
        return Status;
    }

    public Item getOwner() {
        return Owner;
    }
    public void incTry(){ Trys+=1;}
    public int getTries(){ return Trys; }
    public InetSocketAddress getAddress() {
        return transportAddress;
    }
}
