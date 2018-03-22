package com.aurawin.core.rsr.transport.methods;

import com.aurawin.core.array.VarString;
import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

import java.util.HashMap;

public class MethodFactory {
    private HashMap<String, Method> Methods;
    protected String Error;
    public MethodFactory() {
        Methods = new HashMap<>();
    }

    public String getLastError() {
        return Error;
    }

    public Result registerMethod(Method itm) {
        for (String k : itm.Keys) {
            Method i = Methods.get(k);
            if (i == null) Methods.put(k, itm);

        }
        return Result.Ok;
    }

    public Result Process(Method itm, Session ssn, Transport transport){
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
        Method itm = Methods.get(Key);
        return Process(itm,ssn,transport);
    }
    public String getAllMethods() {
        VarString sa = new VarString("", VarString.CreateOptionsOff,", ");
        for (Method itm : Methods.values()){
            for (String k:itm.Keys){
                if (sa.indexOf(k)==-1)
                    sa.add(k);
            }
        }
        return sa.Extract(VarString.ExtractOptionsOff);
    }

}
