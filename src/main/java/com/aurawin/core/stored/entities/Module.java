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
@Table(name = Database.Table.Module)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Module.ByNamespace.name,
                        query = Database.Query.Module.ByNamespace.value
                ),
                @NamedQuery(
                        name  = Database.Query.Module.ById.name,
                        query = Database.Query.Module.ById.value
                )
        }
)

public class Module extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Module.Id)
    private long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.Module.Namespace, unique = true)
    private String Namespace;

    public Module() {
        Id=0;
        Namespace="";
    }
    public Module(String namespace){
        Id=0;
        Namespace=namespace;
    }


    public String getNamespace() {
        return Namespace;
    }

    public void Assign(Module src){
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
                ( u instanceof Module) &&
                        (Id == ((Module) u).Id) &&
                        (Namespace.compareTo( ((Module) u).Namespace)==0)
        );
    }
    public void Verify(Session ssn){
        if (Id == 0) {
            Module m = null;
            Transaction tx = ssn.beginTransaction();
            try {
                Query q = Database.Query.Module.ByNamespace.Create(ssn, Namespace);
                m = (Module) q.uniqueResult();
                if (m == null) {
                    m = new Module(Namespace);
                    ssn.save(m);
                }
                Assign(m);
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
