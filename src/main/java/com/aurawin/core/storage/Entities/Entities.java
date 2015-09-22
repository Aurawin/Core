package com.aurawin.core.storage.entities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.storage.Manifest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Entities {
    protected Manifest Owner;

    //protected ArrayList<Stored> Manifest = new ArrayList<Stored>();

    public Entities(Manifest manifest) {
        Owner=manifest;


//        Iterator it = Owner.Annotated.iterator();
//        while (it.hasNext()){
//            Class<? extends Stored> coe = (Class) it.next();
//            try {
//                Stored e = coe.newInstance();
//                Manifest.add(e);
//            } catch (Exception e) {
//
//            }
//        }
    }

    public static void entityCreated(Entities entities, Session ssn, Transaction tx, Stored obj) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<? extends Stored> coe = (Class<? extends Stored>) it.next();
            Method m=coe.getMethod("entityCreated", Session.class, Transaction.class, Stored.class);
            m.invoke(obj,ssn, tx, obj);
        }
    }
    public static void entityDeleted(Entities entities, Session ssn, Transaction tx, Stored obj) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<? extends Stored> coe = (Class<? extends Stored>) it.next();
            Method m=coe.getMethod("entityDeleted", Session.class, Transaction.class, Stored.class);
            m.invoke(obj,ssn, tx, obj);
        }

    }

    public static class Domain {
        public  static class UserAccount {
            public static com.aurawin.core.storage.entities.domain.UserAccount Create(Entities entities, Session ssn, long DomainId, String Name) throws Exception {
                Query q;
                com.aurawin.core.storage.entities.domain.UserAccount ua;
                Transaction tx = ssn.beginTransaction();
                try {
                    q = Database.Query.Domain.UserAccount.ByName.Create(ssn, DomainId, Name);
                    ua = (com.aurawin.core.storage.entities.domain.UserAccount) q.uniqueResult();
                    if (ua == null) {
                        ua = new com.aurawin.core.storage.entities.domain.UserAccount();
                        ua.setUser(Name);

                        entityCreated(entities, ssn, tx, ua);

                        ssn.save(ua);


                        tx.commit();

                        return ua;
                    } else {
                        throw new Exception(Table.Format(Table.Exception.Entities.Domain.UserAccount.UnableToCreateUserExists, Name));
                    }

                } catch (Exception e){
                    tx.rollback();
                    return null;
                }

            }
            public void Delete(Entities entities, Session ssn,com.aurawin.core.storage.entities.domain.UserAccount Account){
                Transaction tx=ssn.beginTransaction();
                try {
                    ssn.delete(Account);
                    entityDeleted(entities, ssn,tx,Account);
                    tx.commit();

                } catch (Exception e){
                    tx.rollback();
                }


            }
        }
    }
}
