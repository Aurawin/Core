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
@Table(name = Database.Table.Plugin)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Plugin.ByNamespace.name,
                        query = Database.Query.Plugin.ByNamespace.value
                ),
                @NamedQuery(
                        name  = Database.Query.Plugin.ById.name,
                        query = Database.Query.Plugin.ById.value
                )
        }
)

public class Plugin extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Plugin.Id)
    private long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.Plugin.Namespace, unique = true)
    private String Namespace;

    public Plugin() {
        Id=0;
        Namespace="";
    }
    public Plugin(String namespace){
        Id=0;
        Namespace=namespace;
    }

    public String getNamespace() {
        return Namespace;
    }

    public void Assign(Plugin src){
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
                ( u instanceof Plugin) &&
                        (Id == ((Plugin) u).Id) &&
                        (Namespace.compareTo( ((Plugin) u).Namespace)==0)
        );
    }
    public void Verify(Session ssn){
        if (Id == 0) {
            Plugin p = null;
            Transaction tx = ssn.beginTransaction();
            try {
                Query q = Database.Query.UniqueId.ByNamespace.Create(ssn, Namespace);
                p = (Plugin) q.uniqueResult();
                if (p == null) {
                    p = new Plugin(Namespace);
                    ssn.save(p);
                }
                Assign(p);
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
