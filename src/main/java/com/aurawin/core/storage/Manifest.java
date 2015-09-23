package com.aurawin.core.storage;


import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.UniqueId;
import com.aurawin.core.storage.entities.domain.*;

import java.util.ArrayList;
import java.util.List;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.storage.entities.domain.network.Member;
import com.aurawin.core.storage.entities.domain.network.Network;
import org.hibernate.Session;

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

    public List<Class <?>> Annotated= new ArrayList<Class <?>>();
    public List<UniqueId> Namespaces = new ArrayList<UniqueId>();

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

        Annotated.add(Domain.class);
        Annotated.add(UniqueId.class);
        Annotated.add(UserAccount.class);
        Annotated.add(RosterField.class);
        Annotated.add(Roster.class);
        Annotated.add(Network.class);

        Annotated.add(Avatar.class);
        Annotated.add(Member.class);
        Annotated.add(Folder.class);

        Namespace.Register(Namespaces);
    }
    public void Verify(Session ssn){
        for (UniqueId uid : Namespaces){
            uid.Verify(ssn);
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
