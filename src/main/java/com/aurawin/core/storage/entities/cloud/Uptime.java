package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.time.Time;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=false) // just overwrite -> faster
@Table(name= Database.Table.Cloud.Uptime)
@EntityDispatch(
        onCreated = false,
        onDeleted = false,
        onUpdated = false
)
@QueryById(
        Name = Database.Query.Cloud.Uptime.ById.name,
        Fields = { "Id" }
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Uptime.ById.name,
                        query = Database.Query.Cloud.Uptime.ById.value
                )
        }
)
public class Uptime extends Stored{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Table.Cloud.Uptime)
    protected long Id;
    @Override
    public long getId() {
        return Id;
    }

    @ManyToOne(fetch=FetchType.EAGER,targetEntity=Node.class,cascade = CascadeType.ALL)
    @JoinColumn(name = Database.Field.Cloud.Uptime.NodeId)
    protected Node Node;
    public Node getNode(){return Node;}
    public void setNode(Node node){ Node=node;}

    @Column(name = Database.Field.Cloud.Uptime.Stamp)
    protected long Stamp;
    public long getStamp() {
        return Stamp;
    }
    public void setStamp(long stamp) {
        Stamp = stamp;
    }

    public Uptime(long id) {
        Id = id;
        Stamp= Time.dtUTC();
        Node=null;
    }

    public Uptime() {
        Id = 0;
        Stamp=Time.dtUTC();
        Node=null;
    }
    public static void entityCreated(Entities List, Stored Entity) {
        if (Entity instanceof Node){
            Node n = (Node) Entity;
            if (n.Uptime==null){
                n.Uptime=new Uptime();
                n.Uptime.Node=n;
                Entities.Create(List,n.Uptime);
                Entities.Update(List,n,Entities.CascadeOff);
            }
        }
    }
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
