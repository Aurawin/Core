package com.aurawin.core.storage.entities.domain.network;

public enum Standing {
    None ((byte)0,""),
    Administrator ((byte)1,""),
    Affiliate ((byte)2,""),
    Assistant ((byte)3,""),
    Friend ((byte)4,""),
    Family ((byte)5,""),
    Acquaintance ((byte)6,""),
    Cohort ((byte)7,"");

    public byte Level =0;
    public String Permission;

    private Standing(byte level, String permission){
        this.Level=level;
        this.Permission=permission;
    }
}
