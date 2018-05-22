package com.aurawin.core.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.aurawin.core.lang.Table.CRLF;
import static org.junit.Assert.*;

public class MemoryStreamTest {
    final String alphabet = "abcdefghigjklmnopqrstuvwzyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final int len = alphabet.length();
    final Random r = new Random();
    final MemoryStream Input = new MemoryStream();
    final MemoryStream Output = new MemoryStream();
    String line = "";


    @Before
    public void Before(){
        for (int iLcv = 1; iLcv <= 15000; iLcv++){
            line = "";
            for (int jLcv = 1; jLcv <= 2048; jLcv++) {
                line += alphabet.charAt(r.nextInt(len));
            }
            Input.Write(line + " " + iLcv + CRLF);
        }
    }
    @After
    public void After(){
        Input.Release();
        Output.Release();
    }
    @Test
    public void Test() throws IOException {
        Input.SaveToFile(new File("/home/atbrunner/Desktop/Input.txt"));
        Output.LoadFromFile(new File("/home/atbrunner/Desktop/Input.txt"));

        assert(Input.toString().equals(Output.toString()));


    }

}