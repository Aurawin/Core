package com.aurawin.core.rsr;

import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeRead;
import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeWrite;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.requesthandlers.RequestHandler;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;

public class Managers extends ConcurrentLinkedQueue<Items> implements ThreadFactory {
    private long nextId;
    private Instant currentInstant;
    private Instant lastCleanup;
    private Instant Expired;
    private Engine Owner;
    public ConcurrentHashMap<ResolveResult,RequestHandler> Requests;

    public Managers(Engine aOwner){
        nextId=1;
        Owner=aOwner;
        lastCleanup=Instant.now();
        Requests = new ConcurrentHashMap<ResolveResult,RequestHandler>();
    }
    @Override
    public Thread newThread(Runnable r){
        return new Thread(r);
    }
    public void newThread(Items itms) {
        add(itms);
        itms.Thread=newThread((Runnable) itms);
        itms.Thread.setName("Items Thread " + nextId);
        itms.Thread.start();
        nextId++;
    }

    private Items getManagerByLowestItemCount(int Threshold){
        Iterator<Items> it = iterator();
        Items itms = null;
        Items result = null;
        int ctLcv = Threshold;

        while (it.hasNext()) {
            itms=it.next();
            if (itms.size()<ctLcv) {
                result = itms;
            }
        }

        return result;
    }
    private Items getManager(){
        Items result = getManagerByLowestItemCount(Settings.RSR.Server.ManagerItemNewThreadThreshold);
        if (result!=null) {
            if (result.size()>=Settings.RSR.Server.ManagerItemCascadeThreshold) result=null;
        }
        if ( (result==null) && (size()<Settings.RSR.Server.ManagerItemCascadeLimit) ){
            result = new Items(this,Owner,Owner.Infinite);

            newThread(result);
        }
        return result;

    }
    public void Accept(SocketChannel aChannel){
        Items itms = getManager();
        if (itms!=null) {
            Item itm = Owner.Transport.newInstance(itms, aChannel);
            itms.qAddItems.add(itm);
        } else {
            try {
                aChannel.close();
                // todo log refusal to accept socket
            } catch (IOException ioe){
                // todo log close exception
            }
        }
    }
    public void adjustReadBufferSize(){
        Iterator<Items> it = iterator();
        Items itms = null;
        while (it.hasNext()) {
            itms=it.next();
            itms.Commands.Queue(cmdAdjustBufferSizeRead.class);
        }
    }
    public void adjustWriteBufferSize(){
        Iterator<Items> it = iterator();
        Items itms = null;
        while (it.hasNext()) {
            itms=it.next();
            itms.Commands.Queue(cmdAdjustBufferSizeWrite.class);
        }
    }
    public void cleanupItemThreads(){
        currentInstant=Instant.now();
        try {
            if (currentInstant.minusMillis(Settings.RSR.Items.AutoremoveCleanupInterval).isAfter(lastCleanup)) {
                lastCleanup = currentInstant;
                Expired = lastCleanup.minusMillis(Settings.RSR.Items.AutoremoveEmptyItemsDelay);
                Iterator<Items> it = iterator();
                Items itms = null;
                while (it.hasNext()) {
                    itms = it.next();
                    if ( (itms!=null) && (itms.RemovalRequested==false) && (Expired.isAfter(itms.LastUsed)) ) {
                        itms.RemovalRequested = true;
                    }
                }
            }
        } catch (Exception e){
        }
    }
}
