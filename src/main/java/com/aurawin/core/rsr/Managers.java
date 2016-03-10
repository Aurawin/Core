package com.aurawin.core.rsr;

import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeRead;
import com.aurawin.core.rsr.commands.cmdAdjustBufferSizeWrite;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.solution.Settings;

import java.nio.channels.SocketChannel;
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
        if (itms!=null) {
            Item itm = Owner.itmRoot.newInstance(itms, ItemKind.Server);
            itm.SocketHandler.Channel = ch;
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
}
