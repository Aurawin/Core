package com.aurawin.core.rsr.client;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.EngineState.*;
import static com.aurawin.core.rsr.def.EngineState.esStop;

public class Client  extends Engine {
    public InetSocketAddress Address;

    public Client(InetSocketAddress aAddress, Class<? extends Item> aTransport, boolean aInfinate)throws
            IOException,NoSuchMethodException, InstantiationException,IllegalAccessException
    {
        super (aAddress, aTransport, ItemKind.Client,aInfinate);
        State = esCreated;
    }

    @Override
    public void run(){
        try {
            while (State != esFinalize) {
                switch (State) {
                    case esRun:
                        Managers.cleanupItemThreads();
                        break;
                    case esConfigure:
                        try {
                            Entities.Verify();
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
                    case esException:
                        break;
                }
                try {
                    sleep(Settings.RSR.Server.AcceptYield);
                } catch (InterruptedException irqe) {
                    Syslog.Append("Client", "monitor", "Interrupted");
                }
            }
        } catch (Exception e){
            State = esException;
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
        if (State!=esFinalize){
            State=esStop;
        }
    }
    public synchronized void CheckForUpdates(){

    }
    public synchronized <T extends Item>T Connect(InetSocketAddress address) throws Exception{
        return Managers.Connect(address);
    }
}
