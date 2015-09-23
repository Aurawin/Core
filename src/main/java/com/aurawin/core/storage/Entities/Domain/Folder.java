package com.aurawin.core.storage.entities.domain;


import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;

import com.aurawin.core.storage.entities.domain.network.Network;
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

@Table(name = Database.Table.Domain.Folder)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.Folder.ByName.name,
                        query = Database.Query.Domain.Folder.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.Folder.ById.name,
                        query = Database.Query.Domain.Folder.ById.value
                )
        }
)
public class Folder extends Stored {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Network.Folders.Id)
    private long Id;

    @Column(name = Database.Field.Domain.Network.Folders.DomainId)
    private long DomainId;

    @Column(name = Database.Field.Domain.Network.Folders.NetworkId)
    private long NetworkId;

    @Column(name = Database.Field.Domain.Network.Folders.Kind)
    private long Kind;

    @Column(name = Database.Field.Domain.Network.Folders.Exposition)
    private byte Exposition;

    @Column(name = Database.Field.Domain.Network.Folders.Created)
    private long Created;

    @Column(name = Database.Field.Domain.Network.Folders.Modified)
    private long Modified;

    @Column(name = Database.Field.Domain.Network.Folders.Path)
    private String Path;


	public static void entityCreated(Entities List,Stored Entity){
        if (Entity instanceof Folder){

        } else if (Entity instanceof Network){
            // the user is new.
            // Add Mailboxes
            // Add Trash bin
            // Add Documents
        }
    }

    public static void entityDeleted(Entities List,Stored Entity){
        if (Entity instanceof Folder){
            // todo delete all files in this folder
        }
    }

    public Folder() {
        Id=0;
        DomainId=0;
        Kind=0;
        Created=0;
        Modified=0;
        Path="";
    }

    public Folder(long domainId, long kind, String path) {
        DomainId = domainId;
        Kind = kind;
        Path = path;
    }

}
