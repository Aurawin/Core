package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Namespace;
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
public class Avatar extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Avatar.Id)
    private long Id;

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


    public String getExt() {
        return Ext;
    }

    public void setExt(String ext) {
        Ext = ext;
    }

    public long getId() {
        return Id;
    }

    public static void entityCreated(Entities List,Stored Entity)throws Exception {
        if (Entity instanceof UserAccount) {
            UserAccount ua = (UserAccount) Entity;
            if (ua.getAvatarId() == 0) {
                Avatar a = Entities.Domain.Avatar.Create(List, ua);

            }
        } else if (Entity instanceof Roster){
            Roster r = (Roster) Entity;
            if (r.getAvatarId()==0) {
                Avatar a = Entities.Domain.Avatar.Create(List, r);
            }
        } else if (Entity instanceof Network){
            Network n = (Network) Entity;
            if (n.getAvatarId()==0){

            }
        }

    }

    public static void entityDeleted(Entities List,Stored Entity)throws Exception {

    }

}
