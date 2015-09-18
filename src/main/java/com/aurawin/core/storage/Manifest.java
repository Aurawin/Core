package com.aurawin.core.storage;


public class Manifest {
    public String Username;
    public String Password;
    public String Host;
    public int Port;
    public int PoolsizeMin;
    public int PoolsizeMax;
    public int PoolAcrement;
    public int StatementsMax;
    public int Timeout;
    public String Automation;
    public String Database;
    public Dialect Dialect;
    public Driver Driver;

    public Manifest(String username, String password, String host, int port, int poolsizeMin, int poolsizeMax, int poolAcrement, int statementsMax, int timeout, String automation, String database, String dialect, String driver) {
        Username = username;
        Password = password;
        Host = host;
        Port = port;
        PoolsizeMin = poolsizeMin;
        PoolsizeMax = poolsizeMax;
        PoolAcrement = poolAcrement;
        StatementsMax = statementsMax;
        Timeout  = timeout;
        Automation = automation;
        Database = database;
        Dialect = Dialect.fromString(dialect);
        Driver = Driver.fromString(driver);
    }
    public String getConnectionURL(){
        String url = "jdbc:".concat(Driver.getTemplate());
        return url.replace("$host",Host).replace("$port",Integer.toString(Port)).replace("$database",Database);
    }
}
