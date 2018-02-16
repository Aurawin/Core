package com.aurawin.core.enryption;

public class Base64 {
    public static String Decode(String Input){
        return new String(java.util.Base64.getDecoder().decode(Input));
    }
    public static String Encode(String Input){
        //String encoded = Base64.getEncoder().encodeToString(bytes);
        return java.util.Base64.getEncoder().encodeToString(Input.getBytes());
    }
}
