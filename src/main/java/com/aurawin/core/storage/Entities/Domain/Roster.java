package com.aurawin.core.storage.entities.domain;


import com.aurawin.core.lang.Table;
import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.network.Exposure;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Collection;

@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value = true)
@SelectBeforeUpdate( value = true)
@javax.persistence.Table(name = Database.Table.Domain.Roster)
public class Roster extends Stored {
    @ManyToOne()
    @JoinColumn(name = Database.Field.Domain.Roster.OwnerId)
    private UserAccount Owner;

    @OneToMany(mappedBy = "Owner")
    @Cascade(CascadeType.PERSIST)
    private Collection<RosterField> Custom;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Roster.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Roster.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.Roster.AvatarId)
    private long AvatarId;

    @Column(name = Database.Field.Domain.Roster.FirstName)
    private String FirstName;

    @Column(name = Database.Field.Domain.Roster.MiddleName)
    private String MiddleName;

    @Column(name = Database.Field.Domain.Roster.FamilyName)
    private String FamilyName;

    @Column(name = Database.Field.Domain.Roster.Alias)
    private String Alias;

    @Column(name = Database.Field.Domain.Roster.Addresses)
    private String Addresses;

    @Column(name = Database.Field.Domain.Roster.City)
    private String City;

    @Column(name = Database.Field.Domain.Roster.State)
    private String State;

    @Column(name = Database.Field.Domain.Roster.Postal)
    private String Postal;

    @Column(name = Database.Field.Domain.Roster.Country)
    private String Country;

    @Column(name = Database.Field.Domain.Roster.Websites)
    private String Websites;

    public long getId(){
        return Id;
    }
    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String familyName) {
        FamilyName = familyName;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getAddresses() {
        return Addresses;
    }

    public void setAddresses(String addresses) {
        Addresses = addresses;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPostal() {
        return Postal;
    }

    public void setPostal(String postal) {
        Postal = postal;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getWebsites() {
        return Websites;
    }

    public void setWebsites(String websites) {
        Websites = websites;
    }

    public long getAvatarId(){ return AvatarId;}
    public void setAvatarId(long id){ AvatarId=id;}

    public long getOwnerId(){ return Owner.getId();}

    public long getDomainId(){ return DomainId;}


    public Roster() {
        Owner = null;
        DomainId=0;
        Alias="";
    }
    public Roster(UserAccount owner,String alias){
        Owner = owner;
        DomainId = owner.getDomainId();
        Alias=alias;
    }

    public static void entityCreated(Entities List,Stored Entity) throws Exception{
        if (Entity instanceof UserAccount){
            UserAccount ua = (UserAccount) Entity;
            if (ua.getMe()==null) {
                Roster me = Entities.Domain.Roster.Create(List,ua,Table.String(Table.Entities.Domain.Roster.Me));
            }
        }
    }

    public static void entityDeleted(Entities List,Stored Entity) throws Exception {

    }
}
