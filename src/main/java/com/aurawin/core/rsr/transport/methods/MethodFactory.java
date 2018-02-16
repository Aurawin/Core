package com.aurawin.core.rsr.transport.methods;

import com.aurawin.core.array.VarString;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import org.hibernate.Session;

import java.util.EnumSet;
import java.util.HashMap;

public class MethodFactory {
    private HashMap<String, Item> Methods;
    protected String Error;
    public MethodFactory() {
        Methods = new HashMap<>();
    }

    public String getLastError() {
        return Error;
    }

    public Result registerMethod(Item itm) {
        for (String k : itm.Keys) {
            Item i = Methods.get(k);
            if (i == null) Methods.put(k, itm);

        }
        return Result.Ok;
    }

    public Result Process(Item itm, Session ssn, Transport transport){
        if (itm != null) {
            try {
                return itm.onProcess(ssn, transport);
            } catch (Exception e) {
                Error = e.getMessage();
                return Result.Exception;
            }
        } else {
            return Result.NotFound;
        }
    }
    public Result Process(String Key, Session ssn, Transport transport) {
        Item itm = Methods.get(Key);
        return Process(itm,ssn,transport);
    }
    public String getAllMethods() {
        VarString sa = new VarString("", VarString.CreateOptionsOff,", ");
        for (Item itm : Methods.values()){
            for (String k:itm.Keys){
                if (sa.indexOf(k)==-1)
                    sa.add(k);
            }
        }
        return sa.Extract(VarString.ExtractOptionsOff);
    }

}
