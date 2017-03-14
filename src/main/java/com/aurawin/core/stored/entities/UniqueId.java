package com.aurawin.core.stored.entities;


import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.QueryById;

import com.google.gson.annotations.Expose;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.UniqueId)
@NamedQueries(
    {
        @NamedQuery(
            name  = Database.Query.UniqueId.ByNamespace.name,
            query = Database.Query.UniqueId.ByNamespace.value
        ),
        @NamedQuery(
            name  = Database.Query.UniqueId.ById.name,
            query = Database.Query.UniqueId.ById.value
        )
    }
)
@QueryById(Name = Database.Query.UniqueId.ById.name,Fields = { "Id" })

public class UniqueId extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.UniqueId.Id)
    @Expose(serialize = true, deserialize = true)
    private long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.UniqueId.Namespace, unique = true, nullable = false)
    @Expose(serialize = true, deserialize = true)
    public String Namespace;

    public UniqueId() {
        Id=0;
        Namespace="";
    }
    public UniqueId(String namespace){
	    Id=0;
        Namespace=namespace;
    }

    public String getNamespace() {
            return Namespace;
    }
    public void setNamespace(String namespace){
        Id = 0;
        Namespace = namespace;
    }

    public void Assign(UniqueId src){
        Id = src.Id;
        Namespace = src.Namespace;
    }
    public void Empty(){
        Id = 0;
        Namespace="";
    }
    @Override
    public boolean equals(Object u) {
        return (
                    ( u instanceof UniqueId) &&
                    (Id == ((UniqueId) u).Id) &&
                    (Namespace.compareTo( ((UniqueId) u).Namespace)==0)
        );
    }
    @Override
    public void Identify(Session ssn){
        if (Id == 0) {
	        UniqueId uid = null;
            Transaction tx = (ssn.isJoinedToTransaction()) ? ssn.getTransaction() : ssn.beginTransaction();
            try {
                    Query q = ssn.getNamedQuery(Database.Query.UniqueId.ByNamespace.name)
                            .setParameter("Namespace",Namespace);
                    try {
                        uid = (UniqueId) q.getSingleResult();
                    } catch (NoResultException nre) {
                        uid = null;
                    }
                    if (uid == null) {
                        uid = new UniqueId(Namespace);
                        ssn.save(uid);
                    }
                    Assign(uid);
                    tx.commit();

            } catch (Exception e){
                    tx.rollback();
                    throw e;
            }
        }
    }

    public static void entityCreated(Stored Entity, boolean Cascade){}
    public static void entityDeleted(Stored Entity, boolean Cascade){}
    public static void entityUpdated(Stored Entity, boolean Cascade){}
}
