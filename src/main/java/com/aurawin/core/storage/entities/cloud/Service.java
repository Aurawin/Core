package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.QueryByName;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.UniqueId;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert
@DynamicUpdate
@SelectBeforeUpdate
@Table(name = Database.Table.Cloud.Service)
@EntityDispatch(
        onCreated = false,
        onDeleted = false,
        onUpdated = false
)
@QueryById(
        Name = Database.Query.Cloud.Service.ById.name,
        Fields = ("Id")
)
@QueryByName(
        Name = Database.Query.Cloud.Service.ByName.name,
        Field = ("Namepsace")
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Service.ById.name,
                        query = Database.Query.Cloud.Service.ById.value
                )
        }
)
public class Service extends Stored{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=Database.Field.Cloud.Service.Id)
    protected long Id;
    public long getId(){return Id;}


    @Column(name = Database.Field.Cloud.Service.ScaleStart)
    protected int ScaleStart;
    public int getScaleStart(){return ScaleStart;}
    public void setScaleStart(int scaleStart){ ScaleMax=scaleStart;}

    @Column(name = Database.Field.Cloud.Service.ScaleMax)
    protected int ScaleMax;
    public int getScaleMax(){return ScaleMax;}
    public void setScaleMax(int scaleMax){ ScaleMax=scaleMax;}

    @Column(name = Database.Field.Cloud.Service.ScaleMin)
    protected int ScaleMin;
    public int getScaleMin(){return ScaleMin;}
    public void setScaleMin(int scaleMin){ ScaleMax=scaleMin;}


    @ManyToOne(fetch=FetchType.EAGER,targetEntity = Node.class, cascade=CascadeType.ALL)
    @JoinColumn(name = Database.Field.Cloud.Service.NodeId)
    protected Node Node;
    public void setNode(Node node){ Node=node;}
    public Node getNode(){return Node;}


    @ManyToOne(fetch=FetchType.EAGER,targetEntity = UniqueId.class, cascade=CascadeType.ALL)
    @JoinColumn(name = Database.Field.Cloud.Service.UniqueId)
    protected UniqueId UniqueId;
    public void setUniqueId(UniqueId id){ UniqueId=id;}
    public UniqueId getUniqueId(){return UniqueId;}

    public Service() {
        Id=0;
        ScaleStart=0;
        ScaleMin=1;
        ScaleMax=10;
        Node=null;
        UniqueId=null;
    }

    public Service(long id) {
        Id = id;
        ScaleStart=0;
        ScaleMin=1;
        ScaleMax=10;
        Node=null;
        UniqueId=null;
    }

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
