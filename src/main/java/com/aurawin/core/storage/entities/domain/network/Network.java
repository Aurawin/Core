package com.aurawin.core.storage.entities.domain.network;

import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;

import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.UserAccount;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;


import com.aurawin.core.time.Time;
import org.hibernate.annotations.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@javax.persistence.Table( name = Database.Table.Domain.Network.List)
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
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

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Id)
    protected long Id;
    public long getId() {
        return Id;
    }


    @ManyToOne(fetch=FetchType.EAGER, targetEntity = UserAccount.class, cascade = CascadeType.ALL)
    @JoinColumn(name = Database.Field.Domain.Network.OwnerId)
    protected UserAccount Owner;

    @OneToMany(mappedBy = "Owner", fetch = FetchType.EAGER, targetEntity = Member.class, cascade = CascadeType.ALL)
    protected List<Member> Members = new ArrayList<Member>();

    @Column(name = Database.Field.Domain.Network.AvatarId)
    private long AvatarId;


    @Column(name = Database.Field.Domain.Network.DomainId)
    private long DomainId;


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

    @Column(name = Database.Field.Domain.Network.CustomFolders, length = 1024*512)
    private String CustomFolders;

    @Transient
    public List<Folder> Folders = new ArrayList<Folder>();

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
    public long getAvatarId() {  return AvatarId; }
    public void setAvatarId(long id){ AvatarId=id; }
    public static void entityCreated(Entities List,Stored Entity) {
        if (Entity instanceof UserAccount) {
            UserAccount ua = (UserAccount) Entity;
            if (ua.getCabinet() == null) {
                Network cab = new Network(
                        ua,
                        Exposure.Private,
                        Table.String(Table.Entities.Domain.Network.Default.Title),
                        Table.Format(Table.Entities.Domain.Network.Default.Description, ua.getUser())
                );
                Entities.Create(List,cab);
            }
        } else if (Entity instanceof Network) {
            Network n = (Network) Entity;
            if (n.Owner.getCabinet() == null) {
                n.Owner.setCabinetId(n.getId());
                n.Owner.Networks.add(n);
                Entities.Update(List, n.Owner,Entities.CascadeOff);
            }
        }
    }
    public static void entityUpdated(Entities List,Stored Entity, boolean Cascade) { }
    public static void entityDeleted(Entities List,Stored Entity, boolean Cascade) { }


}
