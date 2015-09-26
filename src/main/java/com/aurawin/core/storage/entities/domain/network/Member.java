package com.aurawin.core.storage.entities.domain.network;


import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.Roster;
import com.aurawin.core.storage.entities.domain.network.Network;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value =true)
@Table(name = Database.Table.Domain.Network.Member)
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
public class Member extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Member.Id)
    protected long Id;
    public long getId() {
        return Id;
    }

    @ManyToOne()
    @JoinColumn(name = Database.Field.Domain.Network.Member.NetworkId)
    private Network Owner;

    @Column(name = Database.Field.Domain.Network.Member.DomainId)
    private long DomainId;

    @Column(name =Database.Field.Domain.Network.Member.UserId)
    private long UserId;

    @Column(name = Database.Field.Domain.Network.Member.Exposition)
    private byte Exposition;

    @Column(name = Database.Field.Domain.Network.Member.Standing)
    private byte Level;

    @Column(name =Database.Field.Domain.Network.Member.ACL)
    private long ACL;

    public Member(Network owner) {
        Owner = owner;
        DomainId = owner.getDomainId();
        UserId = owner.getOwnerId();
    }

    public Member() {
    }

    public long getDomainId() {
        return DomainId;
    }

    public void setDomainId(long domainId) {
        DomainId = domainId;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public byte getExposition() { return Exposition; }
    public void setExposition(byte exposition) {
        Exposition = exposition;
    }

    public byte getStanding() {
        return Level;
    }

    public void setStanding(byte standing) {
        Level = standing;
    }

    public long getACL() {
        return ACL;
    }

    public void setACL(long ACL) {
        this.ACL = ACL;
    }

    public static void entityCreated(Entities List,Stored Entity) {
        if (Entity instanceof Member) {
            Member m = (Member) Entity;
            m.Owner.Members.add(m);
        } else if (Entity instanceof Network) {
            Network n = (Network) Entity;
            Member m = new Member(n);
            m.setExposition(Exposure.Private);
            m.setStanding(Standing.Administrator.Level);
            m.setACL(Standing.Administrator.Permission);
            Entities.Create(List, m);
        }
    }

    public static void entityUpdated(Entities List,Stored Entity, boolean Cascade) {}
    public static void entityDeleted(Entities List,Stored Entity, boolean Cascade) {}


}
