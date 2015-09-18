package com.aurawin.core.storage;


import com.aurawin.core.lang.Table;

public class Manifest {
    public String Username;
    public String Password;
    public String Host;
    public int Port;
    public int Poolsize;
    public String Database;
    public Dialect Dialect;
    public Driver Driver;

    public Manifest(String username, String password, String host, int port, int poolsize, String database, String dialect, String driver) {
        Username = username;
        Password = password;
        Host = host;
        Port = port;
        Poolsize = poolsize;
        Database = database;
        Dialect = Dialect.fromString(dialect);
        Driver = Driver.fromString(driver);
    }
}
