package com.aurawin.core.rsr.transport.methods;


import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

import java.util.ArrayList;

public abstract class Item implements Method{
    public static ArrayList<String> Keys;
    public Item(String key) {
        Keys = new ArrayList<>();
        Keys.add(key);
    }
    public Item(String[] keys){
        Keys = new ArrayList<>();
        for (String k:keys) Keys.add(k);

    }
    public abstract Result onProcess(Session ssn, Transport transport);
}
