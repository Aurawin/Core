package com.aurawin.core.storage.entities.domain.network;

import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;

import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.RosterField;
import com.aurawin.core.storage.entities.domain.UserAccount;
import com.aurawin.core.storage.entities.domain.network.Exposure;

import javax.persistence.*;
import javax.persistence.Entity;


import com.aurawin.core.time.Time;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@javax.persistence.Table( name = Database.Table.Domain.Network.List)
public class Network extends Stored {
    public static class Default {
        public static class Flag {
            public static int None = 0;
            public static int Trash = 1 << 0;
            public static int Documents = 1 << 1;
            public static int Mail = 1 << 2;
            public static int Music = 1 << 3;
            public static int Pictures = 1 << 4;
            public static int Videos = 1 << 5;
            public static int CustomFolders = 1 << 6;
            public static int Standard(){
                return Trash | Documents | Mail | Music | Pictures | Videos;
            }
        }
    }
    @ManyToOne
    @JoinColumn(name = Database.Field.Domain.Network.OwnerId)
    private UserAccount Owner;

    @OneToMany(mappedBy = "Owner")
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private List<Member> Members = new ArrayList<Member>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Network.DomainId)
    private long DomainId;


    @Column(name = Database.Field.Domain.Network.AvatarId)
    private long AvatarId;

    @Column(name = Database.Field.Domain.Network.Exposition)
    private byte Exposition;

    @Column(name = Database.Field.Domain.Network.Flags)
    private int Flags;


    @Column(name = Database.Field.Domain.Network.Created)
    private long Created;

    @Column(name = Database.Field.Domain.Network.Modified)
    private long Modified;

    @Column(name = Database.Field.Domain.Network.Title)
    private String Title;

    @Column(name = Database.Field.Domain.Network.Description)
    private String Description;

    @Column(name = Database.Field.Domain.Network.CustomFolders)
    private String CustomFolders;

    public Network() {
        Id=0;
        DomainId=0;
        AvatarId=0;
        Exposition= Exposure.None;
        Created = 0;
        Modified =0;
        Title = "";
        Description = "";
    }
    public Network(UserAccount owner, byte exposition, String title, String description){
        Id=0;
        DomainId=owner.getDomainId();
        Owner = owner;
        Exposition = exposition;
        Created = Time.dtUTC();
        Modified = Created;
        Title = title;
        Description = description;
    }
    public long getOwnerId(){
        return (Owner==null)? 0 : Owner.getId();
    }
    public long getDomainId() {
        return DomainId;
    }
    public long getAvatarId() {
        return AvatarId;
    }
    public static void entityCreated(Entities List,Stored Entity) {
        if (Entity instanceof UserAccount){
            UserAccount ua = (UserAccount) Entity;
            if (ua.getCabinet()==null) {

                Session ssn = List.Sessions.openSession();
                try {
                    Transaction tx = ssn.beginTransaction();
                    Network cab =new Network(
                            ua,
                            Exposure.Private,
                            Table.String(Table.Entities.Domain.Network.Default.Title),
                            Table.Format(Table.Entities.Domain.Network.Default.Description, ua.getUser())
                    );

                    ssn.save(cab); // get Id()
                    Member m= new Member(cab);
                    cab.Members.add(m);
                    ua.Networks.add(cab);

                    m.setDomainId(cab.DomainId);
                    m.setUserId(ua.getId());
                    m.setExposure(Exposure.Private);
                    m.setStanding(Standing.Administrator.Level);
                    m.setACL(Standing.Administrator.Permission);
                    ssn.save(m);

                    ua.setCabinetId(cab.getId());

                    ssn.update(cab);
                    ssn.update(ua);
                    tx.commit();
                } finally{
                    ssn.close();
                }

            }
        }
    }

    public long getId() {
        return Id;
    }

    public static void entityDeleted(Entities List,Stored Entity) {

    }


}
