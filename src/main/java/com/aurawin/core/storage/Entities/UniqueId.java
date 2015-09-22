package com.aurawin.core.storage.entities;

import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.sun.istack.internal.NotNull;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;



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

public class UniqueId  {
        @javax.persistence.Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = Database.Field.UniqueId.Id)
        private long Id;

        @NotNull
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

        public long getId() {
                return Id;
        }

        public String getNamespace() {
                return Namespace;
        }

        public void Assign(UniqueId src){
                Id = src.Id;
                Namespace = src.Namespace;
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

}
