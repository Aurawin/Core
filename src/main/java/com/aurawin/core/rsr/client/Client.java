package com.aurawin.core.rsr.client;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.ItemKind;

import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.EngineState.esStop;

public class Client  extends Engine {

    public InetSocketAddress Address;

    public Client(InetSocketAddress aAddress, Class<? extends Item> aTransport, boolean aInfinate) throws
            InvocationTargetException,IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (aAddress, aTransport, ItemKind.Client,aInfinate);
        State = esCreated;

    }

    @Override
    public void run(){
        try {
            while (State != esStop) {
                switch (State) {
                    case esRun:
                        break;
                    case esConfigure:
                        try {
                            State = esStart;
                        } catch (Exception e) {

                        }
                        break;
                    case esStart:
                        State = esRun;
                        break;
                    case esStop:
                        Managers.Reset();
                        break;
                    case esUpgrade:
                        State = esUpgrading;
                        break;
                    case esUpgrading:
                        //for (cls : ns[])
                        //unload_old_class
                        //load_class
                        State = esRun;
                        break;
                }
                try {
                    sleep(Settings.RSR.Server.ConnectYield);
                } catch (InterruptedException irqe) {
                    Syslog.Append(getClass().getCanonicalName(), "run", "Interrupted");
                }
            }
            if (State==esStop){
                Managers.Reset();
            }
        } catch (Exception e){
            Stop();
            Syslog.Append("Client", "run", Table.Format(Table.Exception.RSR.MonitorLoop, e.getMessage()));
        }
    }
    public synchronized void Configure(){
        if (State==esCreated){
            State=esConfigure;
            start();
        }
    }
    public synchronized void Start(){
        if (State==esCreated){
            State=esStart;
            start();
        }
    }
    public synchronized void Stop(){
        State=esStop;
    }

    public synchronized void CheckForUpdates(){

    }
    public synchronized Item Connect(InetSocketAddress address,boolean persistent) throws Exception{
        return Managers.Connect(address,persistent);
    }
}
