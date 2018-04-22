package com.aurawin.core.stored.entities.security;

import com.aurawin.core.lang.Database;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.Namespaced;
import com.aurawin.core.stored.annotations.QueryById;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.parameter.Parameters;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;

@Entity
@Namespaced
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.LoginFailure)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.LoginFailure.ById.name,
                        query = Database.Query.LoginFailure.ById.value
                ),
                @NamedQuery(
                        name  = Database.Query.LoginFailure.ByIp.name,
                        query = Database.Query.LoginFailure.ByIp.value
                ),
                @NamedQuery(
                        name  = Database.Query.LoginFailure.BetweenInstant.name,
                        query = Database.Query.LoginFailure.BetweenInstant.value
                )

        }
)
@QueryById(
        Name = Database.Query.Ban.ById.name,Fields = { "Id" }
)

public class LoginFailure extends Stored {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.LoginFailure.Id)
    public long Id;

    @Override
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.LoginFailure.DomainId)
    public long DomainId;


    @Column(name = Database.Field.LoginFailure.UserId)
    public long UserId;

    @Column(name = Database.Field.LoginFailure.Ip)
    public long Ip;

    @Column(name = Database.Field.LoginFailure.Instant)
    public Instant Instant;

    @Column(name = Database.Field.LoginFailure.Username)
    public String Username;

    @Column(name = Database.Field.LoginFailure.Password)
    public String Password;

    @Column(name = Database.Field.LoginFailure.Digest)
    public String Digest;

    @Override
    public boolean equals(Object u) {
        return ((u instanceof LoginFailure) && (Id == ((LoginFailure) u).Id));
    }

    @Override
    public void Identify(Session ssn) {
        if (Id == 0) {
            Transaction tx = (ssn.isJoinedToTransaction()) ? ssn.getTransaction() : ssn.beginTransaction();
            try {
                ssn.save(this);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public static void entityCreated(Stored Entity, boolean Cascade) {
    }

    public static void entityDeleted(Stored Entity, boolean Cascade) {
    }

    public static void entityUpdated(Stored Entity, boolean Cascade) {
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<LoginFailure> listAll(long Ip){
        ArrayList<LoginFailure> r = new ArrayList(Entities.Fetch(
                LoginFailure.class,
                Database.Query.LoginFailure.ByIp.name,
                new Parameters(Database.Field.LoginFailure.Ip,Ip)
        ));

        return r;
    }
}