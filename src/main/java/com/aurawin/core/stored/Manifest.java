package com.aurawin.core.stored;


import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.security.Certificate;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.Module;
import com.aurawin.core.stored.entities.UniqueId;
import java.util.ArrayList;

public class Manifest {
    public String Username;
    public String Password;
    public String Host;
    public boolean AutoCommit;
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

    public AnnotatedList Annotated= new AnnotatedList();

    public Manifest(
            String username,
            String password,
            String host,
            int port,
            boolean autocommit,
            int poolsizeMin,
            int poolsizeMax,
            int poolAcrement,
            int statementsMax,
            int timeout,
            String automation,
            String database,
            String dialect,
            String driver,
            AnnotatedList annotations
    ) {
        Username = username;
        Password = password;
        AutoCommit = autocommit;
        Host = host;
        Port = port;
        PoolsizeMin = poolsizeMin;
        PoolsizeMax = poolsizeMax;
        PoolAcrement = poolAcrement;
        StatementsMax = statementsMax;
        Timeout  = timeout;
        Automation = automation;
        Database = database;
        Dialect = com.aurawin.core.stored.Dialect.fromString(dialect);
        Driver = com.aurawin.core.stored.Driver.fromString(driver);

        Class<? extends Stored> cs=null;

        for( Class<? extends Stored> ac : annotations)  {
            cs = Annotated.stream()
                    .filter(c->c.getCanonicalName().equalsIgnoreCase(ac.getCanonicalName()))
                    .findFirst()
                    .orElse(null);
            if (cs==null) {
                Annotated.add(ac);
            }
        }
    }

    public String getConnectionURL(){
        return "jdbc:".concat(Driver.getTemplate()
                .replace("$host",Host)
                .replace("$port",Integer.toString(Port))
                .replace("$database",Database)
        );
    }
}
