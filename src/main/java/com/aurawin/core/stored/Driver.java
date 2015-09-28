package com.aurawin.core.stored;

public enum Driver {
    Firebird ("org.firebirdsql.jdbc.FBDriver", "firebirdsql:$host/$port:$database"),
    Postgresql ("org.postgresql.Driver","postgresql://$host:$port/$database"),
    MySQL("com.mysql.jdbc.Driver","mysql://$host:$port/$database"),
    Oracle("oracle.jdbc.driver.OracleDriver","oracle:thin:@$host:$port:$database"),
    Microsoft("net.sourceforge.jtds.jdbc.Driver","jtds:sqlserver://$host:$port/$database");
    private final String[] value;
    Driver(String... vals) {
        value = vals;
    }
    public String getValue(){return value[0];}
    public String getTemplate(){return value[1];}
    public static Driver fromString(String value){
        for (Driver d : Driver.values()){
            if (d.getValue().compareToIgnoreCase(value)==0){
                return d;
            }
        }
        return null;
    }


}
