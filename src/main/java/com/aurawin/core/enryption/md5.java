package com.aurawin.core.enryption;

import com.aurawin.core.stream.MemoryStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class md5 {
    private final static char[] charMAP = "0123456789ABCDEF".toCharArray();

    public static String Digest(MessageDigest Digest){
        byte[] bytes=Digest.digest();
        int b;
        char [] hex = new char[bytes.length*2];
        for (int i=0; i<bytes.length; i++){
            b = bytes[i] & 0xFF;
            hex[i*2] = charMAP[b >>>4];
            hex[(i*2)+1] = charMAP[b & 0x0F];
        }
        return new String(hex);
    }
    public static String Print(MemoryStream Stream){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            Stream.Position = 0;
            byte[] Data = Stream.Read();
            md.reset();
            md.update(Data);

            return Digest(md);

        } catch (NoSuchAlgorithmException e) {
            return "";
        }


    }
}
