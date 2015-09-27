package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.*;
import com.aurawin.core.storage.annotations.*;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@DynamicInsert(value =true)
@DynamicUpdate(value= true)
@SelectBeforeUpdate(value =true)
@Table(name = Database.Table.Cloud.Resource)
@EntityDispatch(
        onCreated = false,
        onDeleted = false,
        onUpdated = false
)
@QueryById(
        Name = Database.Query.Cloud.Resource.ById.name,
        Fields = { "Id" }
)
@QueryByName(
        Name = Database.Query.Cloud.Resource.ByName.name,
        Field = "Name"
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Resource.ByName.name,
                        query = Database.Query.Cloud.Resource.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Cloud.Resource.ById.name,
                        query = Database.Query.Cloud.Resource.ById.value
                )
        }
)
public class Resource extends Stored{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Cloud.Resource.Id)
    protected long Id;
    @Override
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.Cloud.Resource.Name)
    protected String Name;
    public String getName() {    return Name;  }
    public void setName(String name) {      Name = name;   }

    @ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL,targetEntity=Group.class)
    @JoinColumn(name = Database.Field.Cloud.Resource.GroupId)
    protected Group Group;
    public Group getGroup() { return Group; }
    public void setGroup(Group group) {
        Group = group;
        if (!group.Resources.contains(this)==true)
            group.Resources.add(this);
    }

    public Resource(long id) {
        Id = id;
        Name = "";
        Group=null;
    }

    public Resource() {
        Id =0;
        Name = "";
        Group=null;
    }

    public Resource(long id, String name) {
        Id = id;
        Name = name;
        Group=null;
    }

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
