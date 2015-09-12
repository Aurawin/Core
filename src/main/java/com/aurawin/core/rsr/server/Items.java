package com.aurawin.core.rsr.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Items extends ConcurrentLinkedQueue<Item> implements Runnable {
    Managers Owner;
    public Items(Managers aOwner){
        super ();
        Owner=aOwner;
    }

    @Override
    public void run() {

    }
}
