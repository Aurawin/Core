package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.QueryByName;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Cloud.Node)
@QueryByName(
        Name = Database.Query.Cloud.Node.ByName.name,
        Field = "Name"
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

    public Node() {
        Id = 0;
        Name = "";
    }

    public Node(long id) {
        Id = id;
    }

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
