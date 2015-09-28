package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.QueryByName;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.cloud.Transactions;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Cloud.Node)
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
@QueryByName(
        Name = Database.Query.Cloud.Node.ByName.name,
        Fields = {"Name"}
)
@QueryById(
        Name = Database.Query.Cloud.Node.ById.name,
        Fields = ("Id")
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Node.ByName.name,
                        query = Database.Query.Cloud.Node.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Cloud.Node.ById.name,
                        query = Database.Query.Cloud.Node.ById.value
                )
        }
)
public class Node extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Cloud.Node.Id)
    protected long Id;
    @Override
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.Cloud.Node.Name)
    protected String Name;
    public String getName() {  return Name; }
    public void setName(String name) {     Name = name; }

    @Column(name = Database.Field.Cloud.Node.IP, length = 45)
    protected String IP;
    public String getIP(){ return IP;}
    public void setIP(String ip){IP=ip;}

    @ManyToOne(targetEntity = Resource.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name  = Database.Field.Cloud.Node.ResourceId)
    protected Resource Resource;
    public Resource getResource(){return Resource;}
    public void setResource(Resource resource){
        Resource=resource;
    }

    @ManyToOne(targetEntity = Transactions.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name  = Database.Field.Cloud.Node.TransactionsId)
    protected Transactions Transactions;
    public Transactions getTransactions(){return Transactions;}
    public void setTransactions(Transactions transactions){ Transactions = transactions;}

    @ManyToOne(targetEntity = Uptime.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name  = Database.Field.Cloud.Node.UptimeId)
    protected Uptime Uptime;
    public Uptime getUptime(){return Uptime;}
    public void setUptime(Uptime uptime){ Uptime = uptime;}

    @OneToMany(targetEntity = Service.class,cascade = CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="Node")
    protected List<Service> Services = new ArrayList<Service>();

    public Node() {
        Id = 0;
        Name = "";
        IP="";
        Transactions=null;
        Resource =null;
    }

    public Node(long id) {
        Id = id;
        Name="";
        IP="";
        Transactions=null;
        Resource=null;
    }

    public static void entityCreated(Entities List, Stored Entity){ }
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
