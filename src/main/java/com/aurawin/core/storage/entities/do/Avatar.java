package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;

import com.aurawin.core.storage.entities.domain.network.Network;
import com.aurawin.core.time.Time;
import org.hibernate.*;
import org.hibernate.Query;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.annotation.Generated;
import javax.persistence.*;


@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value=true)
@Table( name = Database.Table.Domain.Avatar)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.Avatar.ByOwnerAndKind.name,
                        query = Database.Query.Domain.Avatar.ByOwnerAndKind.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.Avatar.ById.name,
                        query = Database.Query.Domain.Avatar.ById.value
                )
        }
)
@EntityDispatch(
        onCreated = false,
        onDeleted = false,
        onUpdated = false
)
public class Avatar extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Avatar.Id)
    protected long Id;
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.Domain.Avatar.OwnerId)
    protected long OwnerId;

    @Column (name= Database.Field.Domain.Avatar.DomainId)
    private long DomainId;

    @Column (name = Database.Field.Domain.Avatar.Kind)
    private long Kind;

    @Column (name = Database.Field.Domain.Avatar.Ext)
    private String Ext;

    @Column (name = Database.Field.Domain.Avatar.Created)
    private long Created;

    @Column (name = Database.Field.Domain.Avatar.Modified)
    private long Modified;

    @Column (name = Database.Field.Domain.Avatar.Data)
    private String Data;

    public Avatar(long domainId, long ownerId, long kind) {
        DomainId = domainId;
        OwnerId = ownerId;
        Kind = kind;
        Created = Time.dtUTC();
        Modified= Created;
    }

    public Avatar() {

    }

    public String getExt() {
        return Ext;
    }

    public void setExt(String ext) {
        Ext = ext;
    }

    public static void entityCreated(Entities List,Stored Entity)throws Exception {
        if (Entity instanceof UserAccount) {
            UserAccount ua = (UserAccount) Entity;
            if (ua.getAvatarId() == 0) {
                Avatar a = new Avatar(ua.getDomainId(),ua.getId(),Namespace.Entities.Domain.UserAccount.Avatar.getId());
                Entities.Create(List, a);
                ua.setAvatarId(a.getId());
                Entities.Update(List,ua,Entities.CascadeOff);
            }
        } else if (Entity instanceof Roster){
            Roster r = (Roster) Entity;
            if (r.getAvatarId()==0) {
                Avatar a = new Avatar(r.getDomainId(),r.getOwnerId(),Namespace.Entities.Domain.Roster.Avatar.getId());
                Entities.Create(List, a);
                r.setAvatarId(a.getId());
                Entities.Update(List,r,Entities.CascadeOff);

            }
        } else if (Entity instanceof Network){
            Network n = (Network) Entity;
            if (n.getAvatarId()==0){
                Avatar a = new Avatar(n.getDomainId(),n.getOwnerId(),Namespace.Entities.Domain.Network.Avatar.getId());
                Entities.Create(List,a);
                n.setAvatarId(a.getId());
                Entities.Update(List,n,Entities.CascadeOff);
            }
        }
    }
    public static void entityUpdated(Entities List,Stored Entity, boolean Cascade)throws Exception {}
    public static void entityDeleted(Entities List,Stored Entity, boolean Cascade)throws Exception {}

}
