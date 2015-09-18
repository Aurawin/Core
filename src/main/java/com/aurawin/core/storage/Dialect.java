package com.aurawin.core.storage;

public enum Dialect {
    hFirebird ("org.hibernate.dialect.FirebirdDialect"),
    hPostgresql ("org.hibernate.dialect.PostgreSQLDialect"),
    hMySQL("org.hibernate.dialect.MySQLDialect"),
    hOracle("org.hibernate.dialect.OracleDialect"),
    hMicrosoft("org.hibernate.dialect.SQLServerDialect");


    private final String value;
    private Dialect(String value){
        this.value = value;
    }
    public String getValue(){return value;}}
