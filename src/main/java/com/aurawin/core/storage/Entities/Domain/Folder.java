package com.aurawin.core.storage.entities.domain;


import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Stored;
import org.hibernate.Session;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Domain.Folders)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.Folders.lookupByName.name,
                        query = Database.Query.Domain.Folders.lookupByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.Folders.lookupById.name,
                        query = Database.Query.Domain.Folders.lookupById.value
                )
        }
)
public class Folder implements Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Folders.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Folders.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.Folders.Kind)
    private long Kind;

    @Column(name = Database.Field.Domain.Folders.Created)
    private double Created;

    @Column(name = Database.Field.Domain.Folders.Modified)
    private double Modified;

    @Column(name = Database.Field.Domain.Folders.Path)
    private String Path;

    @Override
    public void entityCreated(Session ssn, Object obj){}
    @Override
    public void entityDeleted(Session ssn, Object obj){}

    public Folder() {
        Id=0;
        DomainId=0;
        Kind=0;
        Created=0.0;
        Modified=0.0;
        Path="";
    }


}
