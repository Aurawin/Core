package com.aurawin.core.rsr.def;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;

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
    protected  Persist Persistent;

    public TransportConnect(Object transportObject, Method transportConstructor, InetSocketAddress transportAddress) {
        this.transportObject = transportObject;
        this.transportConstructor = transportConstructor;
        this.transportAddress= transportAddress;
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
        if ( (Owner!=null) && (Owner.Owner.Engine.Persistent)) Persistent = new Persist(Settings.RSR.persistDelay);
    }

    public Persist getPersistent(){
        return Persistent;
    }
    public TransportConnectStatus getStatus() {
        return Status;
    }

    public boolean exceededTrys(){
        if ((Owner!=null) && (Owner.getPersistant()!=null)) {
            return Owner.getPersistant().exceededTrys();
        } else {
            return (Trys < Settings.RSR.Items.TransportConnect.MaxTries);
        }
    }
    public boolean isReadyToConnect(){
        if ((Owner!=null) && (Owner.getPersistant()!=null)){
            return Owner.getPersistant().readyToTry();
        } else {
            return true;
        }
    }
    public void attemptConnect(){
        Trys +=1;
        if (Owner.getPersistant()!=null){
            Owner.getPersistant().reTry();
        }
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
