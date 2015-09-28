package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.QueryByName;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.Roster;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value =true)
@SelectBeforeUpdate(value =true)
@Table(name = Database.Table.Cloud.Group)
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
@QueryById(
        Name = Database.Query.Cloud.Group.ById.name,
        Fields = { "Id" }
)
@QueryByName(
        Name = Database.Query.Cloud.Group.ByName.name,
        Fields = {"Name"}
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Group.ByName.name,
                        query = Database.Query.Cloud.Group.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Cloud.Group.ById.name,
                        query = Database.Query.Cloud.Group.ById.value
                )
        }
)
public class Group extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Cloud.Group.Id)
    protected long Id;
    @Override
    public long getId() {
        return Id;
    }


    @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL, targetEntity=Location.class)
    @JoinColumn(name = Database.Field.Cloud.Group.LocationId)
    protected Location Location;
    public Location getLocation() { return Location; }
    public void setLocation(Location location){
        Location=location;
        if (Location.Groups.indexOf(this)==-1)
            Location.Groups.add(this);
    }

    @OneToMany(
            targetEntity = Resource.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "Group"
    )
    protected List<Resource> Resources = new ArrayList<Resource>();

    @Column(name = Database.Field.Cloud.Group.Name)
    protected String Name;
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    @Column(name = Database.Field.Cloud.Group.Rack)
    protected String Rack;
    public String getRack() {
        return Rack;
    }
    public void setRack(String rack) {
        Rack = rack;
    }


    @Column(name = Database.Field.Cloud.Group.Row)
    protected String Row;
    public String getRow() { return Row; }
    public void setRow(String row) {
        Row = row;
    }

    public Group(long id) {
        Id = id;
        Name = "";
        Rack="";
        Row ="";
        Location=null;
    }
    public Group() {
        Id =0;
        Name = "";
        Rack="";
        Row = "";
        Location=null;
    }

    public static void entityCreated(Entities List, Stored Entity){ }
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
