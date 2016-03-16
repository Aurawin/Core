package com.aurawin.core.rsr;

import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeRead;
import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeWrite;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.solution.Settings;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;

public class Managers extends ConcurrentLinkedQueue<Items> implements ThreadFactory {
    private long nextId;
    private Instant currentInstant;
    private Instant lastCleanup;
    private Instant Expired;
    private Engine Owner;
    public Managers(Engine aOwner){
        nextId=1;
        Owner=aOwner;
        lastCleanup=Instant.now();
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
            Item itm = Owner.itmRoot.newInstance(itms, aChannel);
            itms.qAddItems.add(itm);
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
        if (currentInstant.minusMillis(Settings.RSR.Items.AutoremoveCleanupInterval).isAfter(lastCleanup)) {
            lastCleanup=currentInstant;
            Expired = lastCleanup.minusMillis(Settings.RSR.Items.AutoremoveEmptyItemsDelay);
            Iterator<Items> it = iterator();
            Items itms = null;
            while (it.hasNext()) {
                itms = it.next();
                if (Expired.isAfter(itms.LastUsed)) {
                    remove(itms);
                    itms.RemovalRequested = true;
                }
            }
        }
    }
}
