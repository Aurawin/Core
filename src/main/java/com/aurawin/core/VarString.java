package com.aurawin.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class VarString {

    public static String fromResource(String name) {
        try {
            InputStream is = VarString.class.getResourceAsStream(name);
            InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader r = new BufferedReader(ir);
            StringBuilder sb = new StringBuilder(1024*1024*100);
            String sLine = "";
            while ((sLine = r.readLine()) != null) {
                sb.append(sLine);
            }
            return sb.toString();
        } catch (Exception e){
            return "";
        }

    }
}
