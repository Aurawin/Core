package com.aurawin.core.enryption;

import com.aurawin.core.lang.Table;
import com.aurawin.core.solution.Settings;

import static java.util.Base64.getMimeEncoder;

public class Base64 {
    public static String Decode(String Input){
        return new String(java.util.Base64.getDecoder().decode(Input));
    }
    public static String Encode(String Input){
        //String encoded = Base64.getEncoder().encodeToString(bytes);
        return java.util.Base64.getEncoder().encodeToString(Input.getBytes());
    }
    public static String Encode (byte[] Input, int lineLength){
        java.util.Base64.Encoder enc =  getMimeEncoder(lineLength, Table.CRLF.getBytes());
        return enc.encodeToString(Input);
    }
}
