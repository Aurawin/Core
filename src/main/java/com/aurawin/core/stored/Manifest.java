package com.aurawin.core.stored;


import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stored.entities.Module;
import com.aurawin.core.stored.entities.UniqueId;
import java.util.ArrayList;
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

    public AnnotatedList Annotated= new AnnotatedList();
    public ArrayList<UniqueId> Namespaces = new ArrayList<UniqueId>();

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

        if (Annotated.contains(UniqueId.class)==false)
          Annotated.add(0,UniqueId.class);

        if (Annotated.contains(Module.class)==false)
            Annotated.add(1,Module.class);

        if (Annotated.contains(Certificate.class)==false)
            Annotated.add(2, Certificate.class);

        for( Class<? extends Stored> ac : annotations)  {
            if (Annotated.contains(ac)==false)
              Annotated.add(ac);
        }
        Namespaces.clear();
        ArrayList<UniqueId> l = Namespace.Discover();
        for (UniqueId u: l){
            Namespaces.add(u);
        }
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
