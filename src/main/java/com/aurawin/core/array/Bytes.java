package com.aurawin.core.array;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bytes {
    public static final byte[] CRLF = {13,10};
    public static final byte[] CRLFCRLF = {13,10,13,10};
    public static final byte[] CRLFDOTCRLF = {13,10,46,13,10};
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
    public static final int Count(byte[] input, byte[] Delimiter){
        boolean found = true;
        int iCounter = 0;
        for (int iLcv=0; iLcv<input.length; iLcv++){
            found = true;
            for (int jLcv=0; jLcv<Delimiter.length; jLcv++){
                if (input[iLcv+jLcv] != Delimiter[jLcv]){
                    found = false;
                    break;
                }
            }
            if (found==true){
                iCounter++;
            }
        }
        return iCounter;
    }
    public static final List<SearchResult> Split(byte[] input, byte[] Term){
        List<SearchResult> sResults = new ArrayList<SearchResult>();
        boolean found = true;
        int iStart = 0;
        for (int iLcv=0; iLcv<input.length; iLcv++){
            found = true;
            for (int jLcv=0; jLcv<Term.length; jLcv++){
                if (input[iLcv+jLcv] != Term[jLcv]){
                    found = false;
                    break;
                }
            }
            if (found==true) {
                sResults.add(new SearchResult(iStart,iLcv));
                iStart=iLcv+Term.length;
                iLcv=iStart-1; // bias b/c it will be incremented
            }
        }
        // add remaining items
        if (iStart<input.length) {
            sResults.add(new SearchResult(iStart,input.length-1));
        }
        return sResults;
    }
    public static final long toLongByTripple(byte[] input){
        long r = 0;
        int size = input.length;
        int p=0;
        int v = 0;
        if (size<=64){
            for (int i = 0; i < size; i++) {
                p = 3 - i;
                v = input[i];

                // 1. 192 * 256^3
                // 2. 168 * 256^2
                // 3. 1 * 256^1
                // 4. 1 * 256^0
                r += v * Math.pow(256, p);
            }
            return r;
        } else {
            return 0;
        }
    }
}
