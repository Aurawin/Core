package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Stored;

import com.sun.istack.internal.NotNull;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import com.google.gson.Gson;
import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Domain.UserAccounts)
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.UserAccount.Id)
    private long Id;

    @NotNull
    @Column(name = Database.Field.Domain.UserAccount.DomainId)
    private long DomainId;


    @Column(name = Database.Field.Domain.UserAccount.NetworkId)
    private long NetworkId;


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
    private double LastLogin;

    @Column(name = Database.Field.Domain.UserAccount.LastConsumptionCalc)
    private double LastConsumptionCalculation;

    @Column(name = Database.Field.Domain.UserAccount.Consumption)
    private long Consumption;

    @Column(name = Database.Field.Domain.UserAccount.Quota)
    private long Quota;

    public UserAccount(long id,long domainId, String user, String pass) {
        this.Id=id;
        this.DomainId=domainId;
        this.User = user;
        this.Pass = pass;
    }
    public UserAccount() {
        this.Id=0;
        this.DomainId=0;
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

    public String getUser() { return User; }
    public void setUser(String user) { this.User = user;}

    public String getPass() {return Pass; }
    public void setPass(String pass) {
        Pass = pass;
    }

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

    public double getLastLogin() {
        return LastLogin;
    }
    public void setLastLogin(double lastLogin) {
        LastLogin = lastLogin;
    }

    public long getConsumption(){return Consumption;}
    public void setConsumption(long consumption){Consumption = consumption;}

    public long getQuota(){return Quota; }
    public void setQuota(long quota){Quota = quota;}



    public static void entityCreated(Session ssn, Transaction tx, Stored Entity){
        if (Entity instanceof UserAccount){
            // todo add social network for "me"
            // todo add mail folders
            // todo add contact for "me";
            // todo add trash bin for "me";
        }
    }

    public static void entityDeleted(Session ssn, Transaction tx, Stored Entity){
        if (Entity instanceof Domain) {
            // todo clear out social network "me";
            // todo etc.
        }
    }
}
