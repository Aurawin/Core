package com.aurawin.core.storage.entities.domain;

import com.aurawin.core.lang.*;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.storage.entities.domain.network.Exposure;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@Table(name = Database.Table.Domain.RosterField)
public class RosterField {
    @ManyToOne()
    @JoinColumn(name = Database.Field.Domain.RosterField.OwnerId)
    private Roster Owner;

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.RosterField.Id)
    private long Id;

    @Column(name = Database.Field.Domain.RosterField.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.RosterField.Key)
    private String Key;

    @Column(name = Database.Field.Domain.RosterField.Value)
    private String Value;

    public RosterField() {
    }

    public static void entityCreated(Entities List,Stored Entity) {
        if (Entity instanceof UserAccount){
            UserAccount ua = (UserAccount) Entity;
        }
    }

    public static void entityDeleted(Entities List,Stored Entity) {

    }

}
