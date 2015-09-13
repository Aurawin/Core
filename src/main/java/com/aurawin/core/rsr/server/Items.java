package com.aurawin.core.rsr.server;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.def.server.ItemState;
import com.aurawin.core.rsr.def.server.ServerState;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;

import static com.aurawin.core.rsr.def.server.ItemState.*;
import static com.aurawin.core.rsr.def.server.rsrResult.*;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {
    private Date dtBegin;
    private Date dtEnd;
    protected Engine Engine;
    protected Managers Owner;
    protected Selector csSelector;

    protected ConcurrentLinkedQueue<Item> qAddItems;
    protected ConcurrentLinkedQueue<Item> qRemoveItems;
    protected ConcurrentHashMap<SocketChannel,Item> ChannelMap;

    public Items(Managers aOwner, Engine aEngine){
        super ();
        Owner = aOwner;
        Engine = aEngine;
        qAddItems = new ConcurrentLinkedQueue<Item>();
        qRemoveItems = new ConcurrentLinkedQueue<Item>();
        try {
            csSelector = Selector.open();
        } catch (IOException ioe){
            Syslog.Append(getClass().getCanonicalName(),"Selector.open", Table.Format(Table.Exception.RSR.Server.UnableToOpenItemChannelSelector, Engine.itmclass.getName()));
        }

    }
    @Override
    public void run() {
        while (Engine.state!= ServerState.ssFinalize){
            dtBegin=new Date();
            processItems();
            dtEnd=new Date();
            // now we can compare at sort/remove/shrink array
        }
    }
    private void logItemException(Item itm,String Namespace, String Unit, String Method){
        try {
            Syslog.Append(
                    Unit,
                    Method,
                    Table.Format(
                            Namespace,
                            itm.Channel.getLocalAddress().toString(),
                            itm.Channel.getRemoteAddress().toString()
                    )
            );
        } catch (IOException ioe){
            // do nothing
        }
    }
    private void processItems(){
        // process add items
        Item itm = qAddItems.poll();
        while (itm!=null){
            try {
                itm.Channel.configureBlocking(false);
                itm.Key=itm.Channel.register(csSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, itm);
                itm.TTL= Time.incMilliSeconds(new Date(), Settings.RSR.Server.Timeout);

            } catch (Exception e){
                // Discard connection
                logItemException(itm,Table.Exception.RSR.Server.UnableToRegisterItemChannel,getClass().getCanonicalName(),getClass().getEnclosingMethod().getName());
            }
            if (itm.onAccepted()==rSuccess){
                itm.state=isEstablished;
            } else {
                logItemException(itm,Table.Error.RSR.Server.AcceptFailure,getClass().getCanonicalName(),getClass().getEnclosingMethod().getName());
            };
            itm=qAddItems.poll();
        }
        // process add items
        itm = qRemoveItems.poll();
        while (itm!=null){
            try {
                itm.Channel.register(csSelector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            } catch (Exception e){
                // Discard connection
                logItemException(itm,Table.Exception.RSR.Server.UnableToRegisterItemChannel,getClass().getCanonicalName(),getClass().getEnclosingMethod().getName());
            }
            if (itm.onAccepted()==rSuccess){
                itm.state=isNone;
            } else {
                logItemException(itm,Table.Error.RSR.Server.AcceptFailure,getClass().getCanonicalName(),getClass().getEnclosingMethod().getName());
            };
            itm=qAddItems.poll();
        }
        // Find sockets to read

    }


}

