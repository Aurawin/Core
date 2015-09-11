package com.aurawin.core.lang;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class Concat {
    public static final byte[] toBytes(String[] args) throws UnsupportedEncodingException{
        String Value="";
        for (String s:args){
            Value+=s;
        }
        return Value.getBytes("UTF-8");
    }
    public static final ByteBuffer toByteBuffer(String[] args) throws UnsupportedEncodingException{
        String Value="";
        for (String s:args) {
            Value+=s;
        }
        return ByteBuffer.wrap(Value.getBytes("UTF-8"));
    }

}
