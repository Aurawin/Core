package com.aurawin.core.rsr.transport.methods;


import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class Method implements MethodProcess, GetCommand, GetKey{
    protected String Command;
    public Result methodState;
    public ArrayList<String> Keys=new ArrayList<>();
    public Method(String key) {
        Command=key;
        Keys.add(key);
    }
    public Method(String[] keys){
        if (keys.length>0) Command = keys[0];
        for (String k:keys) Keys.add(k);
    }
    public String getKey(){
        return (Keys.size()>0)? Keys.get(0) : null;
    }
    public String getCommand(){
        return Command;
    }
    public abstract Result onProcess(Session ssn, Transport transport) throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException;
}
