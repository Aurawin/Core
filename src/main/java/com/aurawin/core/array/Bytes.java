package com.aurawin.core.array;

import java.io.UnsupportedEncodingException;

public class Bytes {
    public static final byte[] CRLF = {13,10};
    public static final byte[] LF = {10};
    public static final int indexOf(byte[] aOuter, byte[] aInner) {
        for(int i = 0; i < aOuter.length - aInner.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < aInner.length; ++j) {
                if (aOuter[i+j] != aInner[j]) {
                  found = false;
                  break;
                }
            }
            if (found) return i;
        }
        return -1;
    }
    public static final int indexOf(byte[] aOuter, byte[] aInner, int SkipCount) {
        int iCounter=0;
        for(int i = 0; i < aOuter.length - aInner.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < aInner.length; ++j) {
                if (aOuter[i+j] != aInner[j]) {
                    found = false;
                    break;
                }
            }
            if ( found==true) {
                iCounter++;
                if (iCounter>SkipCount)  return i;
            }
        }
        return -1;
    }
    public static final String toString(byte[] input){
        try{
            return new String(input,"UTF-8");
        }catch (UnsupportedEncodingException uee){
            return "";
        }
    }

}
