package com.aurawin.core.storage;

public enum Dialect {
    hFirebird ("org.firebirdsql.jdbc.FBDriver"),
    hPostgresql ("org.postgresql.Driver"),
    hMySQL("com.mysql.jdbc.Driver"),
    hOracle("oracle.jdbc.driver.OracleDriver"),
    hMicrosoft("org.hibernate.dialect.SQLServerDialect");


    private final String value;
    private Driver(String value){
        this.value = value;
    }
    public String getValue(){return value;}}
