package com.aurawin.core.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Join {
    public static String[] Strings(String[]... Args){
        ArrayList<String> al = new ArrayList<String>();
        for (String[] arg: Args){
            for( String s:arg) {
                al.add(s);
            }
        }
        String[] result = new String[al.size()];
        return al.toArray(result);
    }

}
