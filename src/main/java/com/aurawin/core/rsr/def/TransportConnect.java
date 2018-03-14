package com.aurawin.core.rsr.def;

import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.solution.Settings;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.time.Instant;

import static com.aurawin.core.rsr.def.TransportConnectStatus.tcsCreated;

public class TransportConnect {
    private int Trys;
    private Instant ttlRetry;
    private TransportConnectStatus Status;
    protected Item Owner;
    protected Engine Engine;
    private Object transportObject;
    private Method transportConstructor;
    private InetSocketAddress transportAddress;
    protected  Persist Persistent;

    public TransportConnect(Engine engine, Object transportObject, Method transportConstructor, InetSocketAddress transportAddress, boolean persistent) {
        this.Engine=engine;
        this.transportObject = transportObject;
        this.transportConstructor = transportConstructor;
        this.transportAddress= transportAddress;
        if (persistent) this.Persistent = new Persist(Settings.RSR.persistDelay);
        this.Trys = 0;
        this.ttlRetry=null;
        this.Status = tcsCreated;
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
    public boolean isAlive(){
        if (Owner==null){
            return true;
        } else if ( Persistent!=null)  {
            return true;
        } else {
            return (Owner.State == ItemState.isEstablished);
        }
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
            return (Trys >= Settings.RSR.Items.TransportConnect.MaxTries);
        }
    }
    public boolean readyForUse(){
        if (Owner!=null){
            return (Owner.State == ItemState.isEstablished);

        } else {

            return false;
        }

    }
    public boolean readyToConnect(){

        if ((Owner!=null) && (Owner.getPersistant()!=null)){
            return Owner.getPersistant().readyToTry();
        } else {
            if (ttlRetry==null) {
                ttlRetry = Instant.now().plusMillis(Settings.RSR.refusedDelay);
                return true;
            } else {
                return Instant.now().isAfter(ttlRetry);
            }
        }
    }
    public void resetTrys(){
        Trys = 0;
        if (Owner.getPersistant()!=null){
            Owner.getPersistant().resetTrys();
        }
    }
    public void attemptConnect(){
        Trys +=1;
        if (Owner.getPersistant()!=null){
            Owner.getPersistant().reTry();
        } else {
            ttlRetry = Instant.now().plusMillis(Settings.RSR.refusedDelay);
        }
    }
    public boolean hasOwner(){
        return (Owner!=null);
    }
    public Item getOwnerOrWait() {
        while ((Owner==null) && (Engine.State!=EngineState.esFinalize))
            try {
                Thread.sleep(Settings.RSR.TransportConnect.SleepDelay);
            } catch (InterruptedException ie){
               return null;
            }

        return Owner;
    }
    public void Release(){
        if (Owner!=null){
            Owner.Release();
            Owner=null;
        }
        Engine = null;
    }
    public void incTry(){ Trys+=1;}
    public int getTries(){ return Trys; }
    public InetSocketAddress getAddress() {
        return transportAddress;
    }
}
