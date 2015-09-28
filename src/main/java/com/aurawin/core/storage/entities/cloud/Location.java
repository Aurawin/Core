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
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert(value =true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Cloud.Location)

@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
@QueryById(
        Name = Database.Query.Cloud.Location.ById.name,
        Fields = { "Id" }
)
@QueryByName(
        Name = Database.Query.Cloud.Location.ByName.name,
        Fields = {"Area","Locality","Region"}
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Location.ById.name,
                        query = Database.Query.Cloud.Location.ById.value
                ),
                @NamedQuery(
                        name  = Database.Query.Cloud.Location.ByName.name,
                        query = Database.Query.Cloud.Location.ByName.value
                )

        }
)
public class Location extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Cloud.Location.Id)
    protected long Id;
    public long getId(){ return Id;}

    @Column(name = Database.Field.Cloud.Location.Area)
    protected String Area;
    public String getArea() {  return Area; }

    public void setArea(String area) { Area = area;}
    @Column(name=Database.Field.Cloud.Location.Street)
    protected String Street;
    public String getStreet() {  return Street;  }

    public void setStreet(String street) { Street = street; }
    @Column(name = Database.Field.Cloud.Location.Building)
    protected String Building;
    public String getBuilding() {return Building;}
    public void setBuilding(String building) {Building = building;}

    @Column(name=Database.Field.Cloud.Location.Country)
    protected String Country;
    public String getCountry() {    return Country;   }
    public void setCountry(String country) { Country = country;   }

    @Column(name=Database.Field.Cloud.Location.Floor)
    protected String Floor;
    public String getFloor() {      return Floor;   }
    public void setFloor(String floor) {        Floor = floor;    }

    @Column(name=Database.Field.Cloud.Location.Locality)
    protected String Locality;
    public String getLocality() {       return Locality;   }
    public void setLocality(String locality) {     Locality = locality;  }

    @Column(name=Database.Field.Cloud.Location.Region)
    protected String Region;
    public String getRegion() {return Region;}
    public void setRegion(String region) {Region = region;}


    @Column(name=Database.Field.Cloud.Location.Room)
    protected String Room;
    public String getRoom() {       return Room; }
    public void setRoom(String room) {        Room = room;   }

    @Column(name=Database.Field.Cloud.Location.Zip)
    protected String Zip;
    public String getZip() {      return Zip;  }
    public void setZip(String zip) {        Zip = zip;    }

    @OneToMany(
            mappedBy = "Location",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            targetEntity=Group.class
    )
    protected List<Group> Groups = new ArrayList<Group>();

    public Location(long id) {
        Id = id;
        Area = "";
        Street = "";
        Building = "";
        Country = "";
        Floor = "";
        Locality = "";
        Region = "";
        Room = "";
        Zip="";
    }

    public Location() {
        Id = 0;
        Area = "";
        Street = "";
        Building = "";
        Country = "";
        Floor = "";
        Locality = "";
        Region = "";
        Room = "";
        Zip="";
    }

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
