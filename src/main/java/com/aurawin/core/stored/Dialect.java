package com.aurawin.core.stored;

public enum Dialect {
    Firebird ("org.hibernate.dialect.FirebirdDialect"),
    Postgresql ("org.hibernate.dialect.PostgreSQLDialect"),
    MySQL("org.hibernate.dialect.MySQLDialect"),
    Oracle("org.hibernate.dialect.OracleDialect"),
    Microsoft("org.hibernate.dialect.SQLServerDialect");


    private final String value;
    Dialect(String value){
        this.value = value;
    }
    public String getValue(){return value;}
    public static Dialect fromString(String value){
        for (Dialect d : Dialect.values()){
            if (d.getValue().compareToIgnoreCase(value)==0){
                return d;
            }
        }
        return null;
    }


}
