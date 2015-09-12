package com.aurawin.core.rsr.server;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.solution.Settings;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Managers extends ConcurrentLinkedQueue<Items>{
    private Engine Owner;
    public Managers(Engine aOwner){
        Owner=aOwner;
    }
    private Items getManagerByLowestCount(){
        Iterator<Items> it = iterator();
        Items itms = null;
        Items result = null;
        int ct = 0;
        int ctLcv = 0;

        while (it.hasNext()) {
            itms=it.next();
            ctLcv=itms.size();
            if (ctLcv<ct)
                result = itms;
        }

        return result;
    }
    private Items getManager(){
        Items result = getManagerByLowestCount();
        if (result!=null) {
            if (result.size()>=Settings.RSR.Server.ManagerItemCascadeThreshold) result=null;
        }
        if ( (result==null) && (size()<Settings.RSR.Server.ManagerItemCascadeLimit) ){
            result = new Items(this);
            add(result);
        }
        return result;

    }
    public void Accept(SocketChannel ch){
        // we have a collection of threads
        // Have mechanism to grow/reuse thread
        Items itms = getManager();
        try {
            Item itm = Owner._itmClass.newInstance();
            itm.setOwner(itms);
            itm.setChannel(ch);
            if (itms != null) {
                itms.add(itm);
            } else {
                itm.onRejected();
            }
        } catch (InstantiationException ise){
            Syslog.Append("Managers", "newInstance", Table.Format(Table.Exception.RSR.Server.UnableToCreateItemInstance, Owner._itmClass.getName()));
        } catch (IllegalAccessException iae){
            Syslog.Append("Managers", "newInstance", Table.Format(Table.Exception.RSR.Server.UnableToAccessItemInstance,Owner._itmClass.getName()));
        }
    }
}
