package com.aurawin.core;

import java.util.Map;
import java.util.stream.Collectors;

public class Environment {
    private static CriticalBlock Lock = new CriticalBlock();
    private static Map<String,String> Data;

    public static void Refresh(){
        Lock.Enter();
        try {
            Data = System.getenv();
        } finally {
            Lock.Leave();
        }
    }
    public static String getString(String key){
        Lock.Enter();
        try{
            Refresh();
            return Data.entrySet().stream().
                    filter( m -> m.getKey().equalsIgnoreCase(key))
                    .map(map -> map.getValue())
                    .collect(Collectors.joining());
        }  finally{
            Lock.Leave();
        }
    }
    public static int getInteger(String key){
        String value = getString(key);
        return ((value==null) || (value.length()==0)) ? 0 : Integer.parseInt(value);
    }
    public static int getInteger(String key, int Default){
        String value = getString(key);
        return ((value==null) || (value.length()==0)) ? Default : Integer.parseInt(value);
    }
}
