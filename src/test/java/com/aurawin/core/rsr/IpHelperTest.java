package com.aurawin.core.rsr;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IpHelperTest {
    String IP = "172.16.1.1";


    @Test
    public void Test(){
        long v = IpHelper.toLong(IP);
        String ip = IpHelper.fromLong(v);
        assert ip.equalsIgnoreCase(IP);
    }

}