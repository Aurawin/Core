package com.aurawin.core.storage;


import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.UniqueId;
import com.aurawin.core.storage.entities.domain.Avatar;
import com.aurawin.core.storage.entities.domain.Domain;
import com.aurawin.core.storage.entities.domain.UserAccount;

import java.util.ArrayList;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.storage.entities.domain.network.Member;
import com.aurawin.core.storage.entities.domain.network.Network;

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

    public ArrayList<Class <? extends Stored>> Annotated= new ArrayList<Class <? extends Stored>>();
    public ArrayList<UniqueId> Namespaces = new ArrayList<UniqueId>();

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
        Annotated.add(UserAccount.class);
        Annotated.add(Avatar.class);
        Annotated.add(Network.class);
        Annotated.add(Member.class);


        Namespaces.add(Namespace.Entities.Folder.Domain);
        Namespaces.add(Namespace.Entities.Folder.Social);
        Namespaces.add(Namespace.Entities.Network.ACL);

    }
    public String getConnectionURL(){
        return "jdbc:".concat(Driver.getTemplate()
                .replace("$host",Host)
                .replace("$port",Integer.toString(Port))
                .replace("$database",Database)
        );
    }
}
