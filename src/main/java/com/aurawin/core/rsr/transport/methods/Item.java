package com.aurawin.core.rsr.transport.methods;


import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

import java.util.ArrayList;

public abstract class Item implements Method{
    public ArrayList<String> Keys=new ArrayList<>();
    public Item(String key) {
        Keys.add(key);
    }
    public Item(String[] keys){
        for (String k:keys) Keys.add(k);
    }
    public abstract Result onProcess(Session ssn, Transport transport);
}
