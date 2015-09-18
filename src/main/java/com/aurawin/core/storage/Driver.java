package com.aurawin.core.storage;

public enum Driver {
    Firebird ("org.firebirdsql.jdbc.FBDriver"),
    Postgresql ("org.postgresql.Driver"),
    MySQL("com.mysql.jdbc.Driver"),
    Oracle("oracle.jdbc.driver.OracleDriver"),
    Microsoft("net.sourceforge.jtds.jdbc.Driver");


    private final String value;
    private Driver(String value){
        this.value = value;
    }
    public String getValue(){return value;}
}
