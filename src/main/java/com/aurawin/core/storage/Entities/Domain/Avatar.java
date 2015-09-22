package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Stored;
import com.sun.istack.internal.NotNull;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

public class Avatar extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Avatar.Id)
    private long Id;

    @NotNull
    @Column (name= Database.Field.Domain.Avatar.DomainId)
    private long DomainId;

    @Column (name = Database.Field.Domain.Avatar.OwnerId)
    private long OwnerId;

    @Column (name = Database.Field.Domain.Avatar.Kind)
    private long Kind;

    @Column (name = Database.Field.Domain.Avatar.Ext)
    private String Ext;

    @Column (name = Database.Field.Domain.Avatar.Created)
    private double Created;

    @Column (name = Database.Field.Domain.Avatar.Modified)
    private double Modified;


    public static void entityCreated(Session ssn, Transaction tx, Stored Entity)throws Exception {

    }


    public static void entityDeleted(Session ssn, Transaction tx, Stored Entity)throws Exception {

    }
}
