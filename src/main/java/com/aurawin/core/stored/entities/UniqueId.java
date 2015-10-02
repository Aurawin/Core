package com.aurawin.core.stored.entities;


import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.aurawin.core.stored.Stored;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.json.JSONObject;

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

public class UniqueId extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.UniqueId.Id)
    private long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.UniqueId.Namespace, unique = true)
    private String Namespace;

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
    public void Verify(Session ssn){
        if (Id == 0) {
	        UniqueId uid = null;
            Transaction tx = ssn.beginTransaction();
            try {
                    Query q = Database.Query.UniqueId.ByNamespace.Create(ssn, Namespace);
                    uid = (UniqueId) q.uniqueResult();
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

    public static void entityCreated(Entities List, Stored Entity){}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade){}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade){}
}
