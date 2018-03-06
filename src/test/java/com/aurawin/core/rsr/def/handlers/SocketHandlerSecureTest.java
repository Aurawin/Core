package com.aurawin.core.rsr.def.handlers;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static java.time.LocalTime.now;
import static org.junit.Assert.*;

public class SocketHandlerSecureTest {

    @Test
    public void beginHandshake() throws InterruptedException {
        Instant Begin = Instant.now();
        Instant Later = Begin.plusMillis(5000);

        int i = 0;
        while (Instant.now().isBefore(Later)==true){
            i+=1;
            Thread.sleep(10);
        }
        Duration d = Duration.between(Begin,Instant.now());
        System.out.println("That took ["+d.getSeconds()+"] seconds and "+i+" iterations!");
    }
}