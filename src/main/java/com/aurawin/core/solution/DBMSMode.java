package com.aurawin.core.solution;

import java.util.EnumSet;

public enum DBMSMode {
    dbmsmNone(0,"",""),
    dbmsmMySQL(1,"com.mysql.jdbc.Driver","org.hibernate.dialect.MySQLDialect"),
    dbmsmMicrosoft(2,"net.sourceforge.jtds.jdbc.Driver","org.hibernate.dialect.SQLServerDialect"),
    dbmsmOracle(3,"oracle.jdbc.driver.OracleDriver","org.hibernate.dialect.OracleDialect"),
    dbmsmPostgreSQL(4,"org.postgresql.Driver","org.hibernate.dialect.PostgreSQLDialect");

    DBMSMode(int value, String driver, String dialect){
        this.value = value;
        this.driver = driver;
        this.dialect=dialect;
    }
    private int value;
    private String dialect;
    private String driver;

    public int getValue(){return value;}
    public String getDriver(){return driver;}
    public String getDialect(){return dialect;}

    public void setMode(int value){
        this.value=value;
    }

    public static DBMSMode fromInt(int value){
        return EnumSet.allOf(DBMSMode.class)
                .stream()
                .filter(m -> m.value==value)
                .findFirst()
                .orElse(dbmsmNone);

    }
}
