package com.aurawin.core.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryStreamTest {
    MemoryStream ms;
    String s1 = "Hello!";
    String s2 = "";
    @Before
    public void Before(){
        ms = new MemoryStream();
    }
    @After
    public void After(){
        ms.Release();
    }
    @Test
    public void Test(){
        ms.Write("Hello!");
        s2=ms.toString();
        assert(s1.equalsIgnoreCase(s2));
    }

}