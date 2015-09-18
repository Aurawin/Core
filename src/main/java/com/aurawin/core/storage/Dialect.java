package com.aurawin.core.storage;

public enum Dialect {
    Firebird ("org.hibernate.dialect.FirebirdDialect"),
    Postgresql ("org.hibernate.dialect.PostgreSQLDialect"),
    MySQL("org.hibernate.dialect.MySQLDialect"),
    Oracle("org.hibernate.dialect.OracleDialect"),
    Microsoft("org.hibernate.dialect.SQLServerDialect");


    private final String value;
    private Dialect(String value){
        this.value = value;
    }
    public String getValue(){return value;}}
