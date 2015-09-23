package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.*;
import com.aurawin.core.lang.Table;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;

import com.aurawin.core.storage.entities.domain.network.Network;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import com.google.gson.Gson;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@javax.persistence.Table(name = Database.Table.Domain.UserAccounts)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.UserAccount.ByName.name,
                        query = Database.Query.Domain.UserAccount.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.UserAccount.ByAuth.name,
                        query = Database.Query.Domain.UserAccount.ByAuth.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.UserAccount.ById.name,
                        query = Database.Query.Domain.UserAccount.ById.value
                )
        }
)
public class UserAccount extends Stored {
    @OneToMany(mappedBy = "Owner")
    @Cascade(CascadeType.PERSIST)
    public List<Network> Networks= new ArrayList<Network>();

    @OneToMany(mappedBy = "Owner")
    @Cascade(CascadeType.PERSIST)
    public List<Roster>Contacts = new ArrayList<Roster>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.UserAccount.Id)
    private long Id;

    @Column(name = Database.Field.Domain.UserAccount.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.UserAccount.CabinetId)
    private long CabinetId;

    @Column(name = Database.Field.Domain.UserAccount.RosterId)
    private long RosterId;

    @Column(name = Database.Field.Domain.UserAccount.AvatarId)
    private long AvatarId;

    @Column(name = Database.Field.Domain.UserAccount.User)
    private String User;

    @Column(name = Database.Field.Domain.UserAccount.Pass)
    private String Pass;

    @Column(name = Database.Field.Domain.UserAccount.Auth)
    private String Auth;


    @Column(name = Database.Field.Domain.UserAccount.FirstIP)
    private long FirstIP;

    @Column(name = Database.Field.Domain.UserAccount.LastIP)
    private long LastIP;

    @Column(name = Database.Field.Domain.UserAccount.LockCount)
    private int Lockcount;

    @Column(name = Database.Field.Domain.UserAccount.LastLogin)
    private long LastLogin;

    @Column(name = Database.Field.Domain.UserAccount.LastConsumptionCalc)
    private long LastConsumptionCalculation;

    @Column(name = Database.Field.Domain.UserAccount.Consumption)
    private long Consumption;

    @Column(name = Database.Field.Domain.UserAccount.Quota)
    private long Quota;


    public UserAccount(long domainId, String user) {
        this.DomainId=domainId;
        this.User = user;
    }
    public UserAccount() {
        this.Id=0;
        this.DomainId=0;
        this.AvatarId=0;
        this.CabinetId=0;
        this.User = "";
        this.Pass = "";
        this.Auth = "";
    }

    public static UserAccount fromJSON(Gson Parser, String Data){
        return (UserAccount) Parser.fromJson(Data,UserAccount.class);
    }

    public boolean equals(UserAccount o){
        return (
                Id==o.Id &&
                DomainId==o.DomainId &&
                CabinetId==o.CabinetId &&
                RosterId==o.RosterId &&
                User.compareTo(o.User)==0 &&
                Pass.compareTo(o.Pass)==0 &&
                Auth.compareTo(o.Auth)==0 &&
                FirstIP==o.FirstIP &&
                LastIP==o.LastIP &&
                Lockcount==o.Lockcount &&
                LastLogin==o.LastLogin &&
                Quota == o.Quota&&
                Consumption == o.Consumption
        );

    }
    public void Assign(UserAccount src){
        Id=src.Id;
        DomainId=src.DomainId;
        RosterId=src.RosterId;
        CabinetId=src.CabinetId;
        User=src.User;
        Pass=src.Pass;
        Auth=src.Auth;
        FirstIP=src.FirstIP;
        LastIP=src.LastIP;
        Lockcount=src.Lockcount;
        LastLogin=src.LastLogin;
        Quota=src.Quota;
        Consumption=src.Consumption;
    }
    public long getId() {   return Id; }
    public long getDomainId() {   return DomainId; }
    public long getAvatarId() { return AvatarId; }
    public void setAvatarId(long id){ AvatarId= id;}

    public String getUser() { return User; }
    public void setUser(String user) { this.User = user;}

    public String getPass() {return Pass; }
    public void setPass(String pass) {
        Pass = pass;
    }
    protected long getCabinetId(long id){
        return CabinetId;
    }
    public void setCabinetId(long id){
        CabinetId=id;
    }

    public long getRosterId(){return RosterId;}
    public void setRosterId(long id){RosterId = id; }

    public String getAuth() {
        return Auth;
    }
    public void setAuth(String auth) {
        Auth = auth;
    }

    public long getFirstIP() {
        return FirstIP;
    }
    public void setFirstIP(long firstIP) {
        FirstIP = firstIP;
    }

    public long getLastIP() {
        return LastIP;
    }
    public void setLastIP(long lastIP) {
        LastIP = lastIP;
    }

    public int getLockcount() {
        return Lockcount;
    }
    public void setLockcount(int lockcount) {
        Lockcount = lockcount;
    }

    public long getLastLogin() {
        return LastLogin;
    }
    public void setLastLogin(long lastLogin) {
        LastLogin = lastLogin;
    }

    public long getConsumption(){return Consumption;}
    public void setConsumption(long consumption){Consumption = consumption;}

    public long getQuota(){return Quota; }
    public void setQuota(long quota){Quota = quota;}

    public Roster getMe(){
        if (Contacts.isEmpty()==true) return null;
        //Predicate<Roster> pred = r -> r.getId()==RosterId;
        return Contacts.stream().filter(r -> r.getId()==RosterId).findFirst().get();
    }

    public Network getCabinet(){
        if (Networks.isEmpty()==true) return null;
        Predicate<Network> IdMatch = (n) -> n.getId()==CabinetId;
        return Networks.stream().filter(IdMatch).findFirst().get();
    }

    public static void entityCreated(Entities List,Stored Entity) throws Exception{
        if (Entity instanceof UserAccount){
            UserAccount ua = (UserAccount) Entity;
            // todo add mail folders
            // todo add trash bin for "me";
        } else if (Entity instanceof Domain){
            Domain d = (Domain) Entity;
            UserAccount ua = Entities.Domain.UserAccount.Create(List,d.getId(),Table.String(Table.Entities.Domain.Root));
            ua.Consumption=0;
        }
    }

    public static void entityDeleted(Entities List,Stored Entity){
        if (Entity instanceof Domain) {
            // todo clear out social network "me";
            // todo etc.
        }
    }
}
