package com.aurawin.core.stored;


import com.aurawin.core.stored.annotations.StoredAnnotations;
import com.aurawin.core.stored.entities.Module;
import com.aurawin.core.stored.entities.Plugin;
import com.aurawin.core.stored.entities.UniqueId;
import java.util.ArrayList;
import java.util.List;
import com.aurawin.core.lang.Namespace;

import org.hibernate.Session;

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

    public StoredAnnotations Annotated= new StoredAnnotations();
    public List<UniqueId> Namespaces = new ArrayList<UniqueId>();

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
            StoredAnnotations annotations
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
        Dialect = Dialect.fromString(dialect);
        Driver = Driver.fromString(driver);

        if (Annotated.contains(UniqueId.class)==false)
          Annotated.add(0,UniqueId.class);

        if (Annotated.contains(Plugin.class)==false)
            Annotated.add(1,Plugin.class);

        if (Annotated.contains(Module.class)==false)
            Annotated.add(2,Module.class);

        for( Class<? extends Stored> ac : annotations)  {
            if (Annotated.contains(ac)==false)
              Annotated.add(ac);
        }

        Namespace.Register(Namespaces);
    }
    public void Verify(Session ssn){
        for (UniqueId uid : Namespaces){
            uid.Identify(ssn);
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
