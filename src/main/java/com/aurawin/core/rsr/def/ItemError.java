package com.aurawin.core.rsr.def;

import com.aurawin.core.lang.Table;

public enum ItemError {
    eReset (Table.String(Table.Error.RSR.Reset)),
    eDNS (Table.String(Table.Error.RSR.DNS)),
    eRead (Table.String(Table.Error.RSR.Read)),
    eWrite (Table.String(Table.Error.RSR.Write)),
    eTimeout (Table.String(Table.Error.RSR.Timeout)),
    eSSL(Table.String(Table.Error.RSR.SSL)),
    eConnect (Table.String(Table.Error.RSR.Connect));


    ItemError(String value){
        this.value = value;
    }
    private final String value;

    public String getValue(){return value;}
}

