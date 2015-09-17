package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;

import com.aurawin.core.rsr.Commands.cmdAdjustBufferSizeRead;
import com.aurawin.core.rsr.Commands.cmdAdjustBufferSizeWrite;
import com.aurawin.core.solution.Settings;

import java.nio.channels.SocketChannel;
import java.security.acl.Owner;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;

public class Managers extends ConcurrentLinkedQueue<Items> implements ThreadFactory {
    private Engine Owner;
    public Managers(Engine aOwner){
        Owner=aOwner;
    }
    @Override
    public Thread newThread(Runnable r){
        return new Thread(r);
    }
    public void newThread(Items itms) {
        add(itms);
        itms.Thread=newThread((Runnable) itms);
        itms.Thread.setName("Engine Thread " + size()+1);
        itms.Thread.start();
    }

    private Items getManagerByLowestCount(int Threshold){
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
        Items result = getManagerByLowestCount(Settings.RSR.Server.ManagerItemNewThreadThreshold);
        if (result!=null) {
            if (result.size()>=Settings.RSR.Server.ManagerItemCascadeThreshold) result=null;
        }
        if ( (result==null) && (size()<Settings.RSR.Server.ManagerItemCascadeLimit) ){
            result = new Items(this,Owner,Owner.Infinite);

            newThread(result);
        }
        return result;

    }
    public void Accept(SocketChannel ch){
        // we have a collection of threads
        // Have mechanism to grow/reuse thread
        Items itms = getManager();
        Item itm = Owner.itmRoot.newInstance(itms);
        itm.setChannel(ch);
        if (itms != null) {
            itms.qAddItems.add(itm);
        } else {
            itm.onRejected();
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
}
