package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.QueryByName;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
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
                        name  = Database.Query.Cloud.Service.ByName.name,
                        query = Database.Query.Cloud.Service.ByName.value
                ),
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

    @Column(name=Database.Field.Cloud.Service.Namespace)
    protected String Namespace;
    public void setNamepsace(String namespace){ Namespace=namespace;}
    public String getNamespace(){return Namespace;}

    @ManyToOne(fetch=FetchType.EAGER,targetEntity = Node.class, cascade=CascadeType.ALL)
    @JoinColumn(name = Database.Field.Cloud.Service.NodeId)
    protected Node Node;
    public void setNode(Node node){ Node=node;}
    public Node getNode(){return Node;}

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
