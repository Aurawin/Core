package com.aurawin.core.stored.entities.security;

import com.aurawin.core.lang.Database;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.QueryById;
import com.aurawin.core.stored.entities.Entities;

import com.aurawin.core.stored.parameter.Parameters;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Ban)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Ban.ById.name,
                        query = Database.Query.Ban.ById.value
                ),
                @NamedQuery(
                        name  = Database.Query.Ban.ByIp.name,
                        query = Database.Query.Ban.ByIp.value
                )
        }
)
@QueryById(
        Name = Database.Query.LoginFailure.ById.name,Fields = { "Id" }
)
public class Ban extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Ban.Id)
    private long Id;

    @Override
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.Ban.Ip)
    public long Ip;

    @Override
    public boolean equals(Object u) {
        return ( ( u instanceof Ban) &&(Id == ((Ban) u).Id) );
    }
    @Override
    public void Identify(Session ssn){
        if (Id == 0) {
            Transaction tx = (ssn.isJoinedToTransaction())? ssn.getTransaction() : ssn.beginTransaction();
            try {
                ssn.save(this);
                tx.commit();
            } catch (Exception e){
                tx.rollback();
                throw e;
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Ban> listAll(long Ip){
        ArrayList<Ban> r = new ArrayList();
        Entities.Fetch(
                Ban.class,
                Database.Query.Ban.ByIp.name,
                new Parameters(Database.Field.Ban.Ip,Ip)
        ).forEach( b ->r.add((Ban) b));
        return r;
    }

    public static void entityCreated(Stored Entity, boolean Cascade){}
    public static void entityDeleted(Stored Entity, boolean Cascade){}
    public static void entityUpdated(Stored Entity, boolean Cascade){}
}
