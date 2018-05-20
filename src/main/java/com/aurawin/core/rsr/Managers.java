package com.aurawin.core.rsr;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.ItemCommand;
import com.aurawin.core.rsr.def.ItemKind;

import com.aurawin.core.rsr.def.Persist;
import com.aurawin.core.solution.Settings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;

import static com.aurawin.core.rsr.def.ItemCommand.cmdAccept;
import static com.aurawin.core.rsr.def.ItemCommand.cmdConnect;
import static com.aurawin.core.rsr.def.ItemKind.Client;

public class Managers extends ConcurrentLinkedQueue<Items> implements ThreadFactory {
    private long nextId;
    private Instant currentInstant;
    private Instant lastCleanup;
    private Instant Expired;
    private Engine Owner;

    private Class[] itemConstructorParams=new Class[3];

    public Managers(Engine aOwner){
        nextId=1;
        Owner=aOwner;
        lastCleanup=Instant.now();

        itemConstructorParams[0] = Items.class;
        itemConstructorParams[1] = SocketChannel.class;
        itemConstructorParams[2] = ItemKind.class;

    }
    @Override
    public Thread newThread(Runnable r){
        return new Thread(r);
    }
    public void newThread(Items itms) {
        add(itms);
        itms.LastUsed = Instant.now();
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
            if (itms.List.size()<ctLcv) {
                result = itms;
            }
        }

        return result;
    }
    private Items getManager(){
        Items result = getManagerByLowestItemCount(Settings.RSR.Server.ManagerItemNewThreadThreshold);
        if (result!=null) {
            if (result.List.size()>=Settings.RSR.Server.ManagerItemCascadeThreshold) result=null;
        }
        if ( (result==null) && (size()<Settings.RSR.Server.ManagerItemCascadeLimit) ){
            result = new Items(this,Owner,Owner.Infinite);

            newThread(result);
        }
        return result;

    }

    @SuppressWarnings("unchecked")
    public Item Connect(InetSocketAddress address, boolean persistent)throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException
    {
        Item itm = null;
        Items itms = getManager();

        if (itms!=null) {
            Class c = Owner.transportClass;
            if (c!=null) {
                Method m = c.getMethod("newInstance", itemConstructorParams);
                try {
                    m.setAccessible(true);
                    itm = (Item) m.invoke(Owner.transportObject,itms,SocketChannel.open(), Client);
                    if (persistent) itm.Persistent=new Persist(Settings.RSR.persistDelay);
                    itm.Address=address;
                    itm.bindAddress=Owner.Address;
                    itm.Commands.add(cmdConnect);
                    itms.List.add(itm);

                } catch (Exception e){
                    Syslog.Append(getClass().getCanonicalName(), "Connect", Table.Format(Table.Exception.RSR.ManagerConnectConstructor, e.getMessage(), address.getHostString()));
                }

            } else {
                Syslog.Append(getClass().getCanonicalName(),"Connect", Table.Format(Table.Exception.RSR.ManagerConnectTransport,address.getHostString()));
            }
        }
        return itm;
    }

    @SuppressWarnings("unchecked")
    public void Accept(SocketChannel aChannel)throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException
    {
        Items itms = getManager();
        if (itms!=null) {
            Class c = Owner.transportClass;
            Object o = Owner.transportObject;
            if ((c!=null) && (o !=null)) {
                try {

                    Method m = c.getMethod("newInstance", itemConstructorParams);
                    m.setAccessible(true);
                    Item itm = (Item) m.invoke(o, itms, aChannel,ItemKind.Server);
                    itms.List.add(itm);
                    itm.Address=(InetSocketAddress) aChannel.getRemoteAddress();
                    itm.Commands.add(cmdAccept);
                } catch (Exception e){
                    Syslog.Append(getClass().getCanonicalName(),"Accept", Table.Format(Table.Exception.RSR.ManagerAccept,e.getMessage()));
                }
            } else {
                // todo log exception
            }
        } else {
            try {
                aChannel.close();
                // todo log refusal to accept socket
            } catch (IOException ioe){
                // todo log close exception
            }
        }
    }


    public void Reset(){
        Iterator<Items> it = iterator();
        Items itms = null;
        while (it.hasNext()){
            itms = it.next();
            itms.Release();
            itms.elasticRebound=true;
        }
    }
}
