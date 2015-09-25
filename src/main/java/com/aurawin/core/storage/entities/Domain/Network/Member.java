package com.aurawin.core.storage.entities.domain.network;


import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Namespace;
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
public class Member extends Stored {
    @ManyToOne()
    @JoinColumn(name = Database.Field.Domain.Network.Member.NetworkId)
    private Network Owner;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Member.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Network.Member.DomainId)
    private long DomainId;

    @Column(name =Database.Field.Domain.Network.Member.UserId)
    private long UserId;

    @Column(name = Database.Field.Domain.Network.Member.Exposure)
    private byte Exposure;

    @Column(name = Database.Field.Domain.Network.Member.Standing)
    private byte Standing;

    @Column(name =Database.Field.Domain.Network.Member.ACL)
    private long ACL;

    public Member(Network owner) {
        Owner = owner;
        DomainId = owner.getDomainId();
        UserId = owner.getOwnerId();
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

    public byte getExposure() {
        return Exposure;
    }

    public void setExposure(byte exposure) {
        Exposure = exposure;
    }

    public byte getStanding() {
        return Standing;
    }

    public void setStanding(byte standing) {
        Standing = standing;
    }

    public long getACL() {
        return ACL;
    }

    public void setACL(long ACL) {
        this.ACL = ACL;
    }

    public static void entityCreated(Entities List,Stored Entity) {
        if (Entity instanceof Network) {
            Network n = (Network) Entity;
            //todo Member m = Entities.Domain.Network.addMember(n);


        }
    }


    public static void entityDeleted(Entities List,Stored Entity) {

    }


}
