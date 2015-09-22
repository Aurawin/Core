package com.aurawin.core.storage.entities.domain.Network;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.UserAccount;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Domain.Network)

public class Network implements Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Network.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.Network.OwnerId)
    private long OwnerId;


    @Column(name = Database.Field.Domain.Network.AvatarId)
    private long AvatarId;

    @Column(name = Database.Field.Domain.Network.Exposure)
    private long Exposure;

    @Column(name = Database.Field.Domain.Network.Created)
    private double Created;

    @Column(name = Database.Field.Domain.Network.Modified)
    private double Modified;

    @Column(name = Database.Field.Domain.Network.Title)
    private String Title;

    @Column(name = Database.Field.Domain.Network.Description)
    private String Description;

    public Network() {
        Id=0;
        OwnerId=0;
        AvatarId=0;
        Exposure=0;
        Created = 0.0;
        Modified = 0.0;
        Title = "";
        Description = "";
    }

    public Network(long ownerId, long avatarId, long exposure, String title, String description){
        Id = 0;
        OwnerId = ownerId;
        AvatarId = avatarId;
        Exposure = exposure;
        Title = title;
        Description = description;
    }

    @Override
    public void entityCreated(Session ssn, Transaction tx, Stored Entity) {
        if (Entity instanceof UserAccount){
            UserAccount ua = (UserAccount) Entity;
            Network net = new Network(ua.getId(),ua.getAvatarId(),0,"","");

            // todo create a me account


        }

    }

    @Override
    public void entityDeleted(Session ssn, Transaction tx, Stored Entity) {

    }

    @Override
    public void entityRegistered(Session ssn, Transaction tx, Stored Entity){

    }

    public static class Member implements Stored{

    }
}
