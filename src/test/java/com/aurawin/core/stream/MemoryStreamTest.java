package com.aurawin.core.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import static com.aurawin.core.lang.Table.CRLF;
import static org.junit.Assert.*;

public class MemoryStreamTest {
    final String alphabet = "abcdefghigjklmnopqrstuvwzyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final int len = alphabet.length();
    final int testSize = 2048;
    final int biasRemoval = testSize+2;
    final Random r = new Random();
    final MemoryStream Input = new MemoryStream();
    final MemoryStream Output = new MemoryStream();
    String line = "";


    @Before
    public void Before(){
        line = "";
        for (int iLcv = 1; iLcv <= testSize; iLcv++) {
            line += alphabet.charAt(r.nextInt(len));
        }
        for (int iLcv = 1; iLcv <= 100; iLcv++){
            Input.Write(line + CRLF);
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
        Output.SaveToFile(new File("/home/atbrunner/Desktop/Output.txt"));
        ByteBuffer bb = ByteBuffer.allocate(5000000);
        Output.position(Output.size()-2050);
        Output.read(bb);
        //Output.position(0);
        MemoryStream ms = new MemoryStream();
        bb.flip();
        ms.write(bb);
        ms.SaveToFile(new File("/home/atbrunner/Desktop/Chunk.txt"));

        for (int iLcv=0; iLcv<10; iLcv++){
            Output.position(biasRemoval);
            Output.sliceAtPosition();
            Output.SaveToFile(new File("/home/atbrunner/Desktop/Output-Sliced ("+ iLcv+").txt"));
        }


    }

}