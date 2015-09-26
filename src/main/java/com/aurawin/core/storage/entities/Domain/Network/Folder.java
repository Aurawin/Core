package com.aurawin.core.storage.entities.domain.network;


import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;

import com.aurawin.core.storage.entities.domain.UserAccount;
import com.aurawin.core.time.Time;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@javax.persistence.Table(name = Database.Table.Domain.Folder)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.Folder.ByPath.name,
                        query = Database.Query.Domain.Folder.ByPath.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.Folder.ById.name,
                        query = Database.Query.Domain.Folder.ById.value
                )
        }
)
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
public class Folder extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Id)
    protected long Id;
    public long getId() {
        return Id;
    }
    @Column(name = Database.Field.Domain.Network.Folders.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.Network.Folders.OwnerId)
    private long OwnerId;

    @Column(name = Database.Field.Domain.Network.Folders.NetworkId)
    private long NetworkId;

    @Column(name = Database.Field.Domain.Network.Folders.Exposition)
    private byte Exposition;

    @Column(name = Database.Field.Domain.Network.Folders.Created)
    private long Created;

    @Column(name = Database.Field.Domain.Network.Folders.Modified)
    private long Modified;

    @Column(name = Database.Field.Domain.Network.Folders.Path)
    private String Path;

    @Transient
    private String Name;

    @Transient
    private Folder Parent;

    @Transient
    private List<Folder> Children = new ArrayList<Folder>();

    public Folder getRoot(){
        Folder r = this;
        while (r.Parent!=null)
            r=r.Parent;
        return r;
    }
    public String buildPath(){
        String sPath="";
        Folder f = this;
        while (f!=null) {
            sPath = f.Name + '/' + sPath;
            f = f.Parent;
        }
        sPath=sPath.substring(0, sPath.length() - 1);
        return sPath;
    }
    public void recalculatePath(){
        Path=buildPath();
        Modified=Time.dtUTC();
        for (Folder f : Children){
            f.recalculatePath();
        }
    }
    public Folder addChild(String name){
        Folder c = new Folder(DomainId,OwnerId,NetworkId,name);
        c.Parent=this;
        c.DomainId =DomainId;
        c.OwnerId = OwnerId;
        c.NetworkId=NetworkId;
        Modified=Time.dtUTC();
        Children.add(c);
        return c;
    }
    public Folder getChildByName(String name){
        for (Folder f : Children){
            if (f.Name.compareTo(name)==0)
                return f;
        }
        return null;
    }
    public String getPath(){ return Path;}
    public void Rename(String name) throws Exception{
        if (Parent==null){
            if (getChildByName(name)==null) {
                Name=name;
                recalculatePath();
            } else {
                throw new Exception(Table.Format(Table.Exception.Entities.Domain.Folder.UnableToCreateExists,name));
            }
        } else {
            if (Parent.getChildByName(name)==null){
                Name=name;
                recalculatePath();
            } else {
                throw new Exception(Table.Format(Table.Exception.Entities.Domain.Folder.UnableToCreateExists,name));
            }
        }
    }
    public Folder() {
        Parent=null;
        Id=0;
        DomainId=0;
        Created=0;
        Modified=0;
        Path="";
    }
    public Folder(long domainId, long ownerId, long networkId, String name) {
        Parent = null;
        DomainId = domainId;
        OwnerId = ownerId;
        NetworkId = networkId;
        Name = name;
        Path = buildPath();
        Created = Time.dtUTC();
        Modified = Created;

    }
    public static void entityCreated(Entities List,Stored Entity){
        if (Entity instanceof UserAccount){
            // new user account is created
            UserAccount a = (UserAccount) Entity;
            Network cab = a.getCabinet();
        } else if (Entity instanceof Network){
            // the user is new.
            // Add Mailboxes
            // Add Trash bin
            // Add Documents
        }
    }

    public static void entityDeleted(Entities List,Stored Entity, boolean Cascade){
        if (Entity instanceof Folder){
            // todo AuDisk delete all native files in this folder
        }
    }
    public static void entityUpdated(Entities List,Stored Entity, boolean Cascade){
        if (Entity instanceof Folder) {
            Folder f = (Folder) Entity;
            if (Cascade == true) {
                // note, just Update all children re-entrant will handle their children
                for (Folder c : f.Children){
                    //todo Entities.Domain.Folder.
                }
            }
        }

    }
}
