package com.aurawin.core.rsr;

public class IpHelper {

    public static final long toLong(String input){
        String[] ip = input.split("\\.");
        byte[] ba = new byte[ip.length];
        for (int i=0; i<ip.length; i++){
            ba[i]= (ip[i].length()==0)? 0: (byte)  Integer.parseInt(ip[i]);
        }
        return toLong(ba);
    }

    public static final long toLong(byte[] input){
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
    public static final String fromLong(long input){
        return (((input >> 24 ) & 0xFF) + "." +
                ((input >> 16 ) & 0xFF) + "." +
                ((input >>  8 ) & 0xFF) + "." +
                ( input        & 0xFF)
        );

    }
}
