package com.aurawin.core.storage.entities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Entities {
    public Manifest Owner;
    public SessionFactory Sessions;

    public Entities(Manifest manifest) {
	    Sessions= Hibernate.openSession(manifest);
        Owner=manifest;
        Session ssn = Sessions.openSession();
        try {
            Owner.Verify(ssn);
        } finally {
            ssn.close();
        }
    }
    private static void entityCreated(Entities entities,Stored obj) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true){
                Method m = goe.getMethod("entityCreated",Entities.class,Stored.class);
	            if (m!=null) m.invoke(obj, entities,obj);
            }
        }
    }
    private static void entityDeleted(Entities entities, Stored obj) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
	        Class<?> goe = (Class<?>) it.next();
	        if (Stored.class.isAssignableFrom(goe)==true) {
		        Method m = goe.getMethod("entityDeleted", Entities.class, Stored.class);
		        if (m!=null) m.invoke(obj, entities, obj);
	        }
        }

    }
    public static class Domain {
        public static com.aurawin.core.storage.entities.domain.Domain Create(Entities entities, String Name, String Root) throws Exception {
            Session ssn = entities.Sessions.openSession();
            Query q;
            com.aurawin.core.storage.entities.domain.Domain d;
            Transaction tx = ssn.beginTransaction();
            q = Database.Query.Domain.ByName.Create(ssn, Name);
            d = (com.aurawin.core.storage.entities.domain.Domain) q.uniqueResult();
            if (d != null) {
                throw new Exception(Table.Format(Table.Exception.Entities.Domain.UnableToCreateDomainExists, Name));
            }
            try {
                d = new com.aurawin.core.storage.entities.domain.Domain(Name, Root);
                ssn.save(d);
                tx.commit();
                ssn.close();

            } catch (Exception e) {
                tx.rollback();
                ssn.close();
                throw e;
            }
            entityCreated(entities, d);
            return d;

        }
        public static class Roster{
            public static com.aurawin.core.storage.entities.domain.Roster Create(Entities entities,com.aurawin.core.storage.entities.domain.UserAccount ua,String alias) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.Roster r = null;
                Transaction tx = ssn.beginTransaction();
                try {
                    r = new com.aurawin.core.storage.entities.domain.Roster(ua,alias);
                    ssn.save(r);
                    if (ua.getMe()==null){
                        ua.Contacts.add(r);
                        ua.setRosterId(r.getId());
                        ssn.update(ua);
                    } else {
                        ua.Contacts.add(r);
                    }
                    tx.commit();
                    ssn.close();
                    entityCreated(entities, r);
                    return r;
                } catch (Exception e){
                    tx.rollback();
                    ssn.close();
                }
                return null;
            }
        }
        public static class UserAccount {
            public static com.aurawin.core.storage.entities.domain.UserAccount Create(Entities entities,long DomainId, String Name) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.UserAccount ua;
                Transaction tx = ssn.beginTransaction();
                q = Database.Query.Domain.UserAccount.ByName.Create(ssn, DomainId, Name);
                ua = (com.aurawin.core.storage.entities.domain.UserAccount) q.uniqueResult();
                if (ua != null) {
                    throw new Exception(Table.Format(Table.Exception.Entities.Domain.UserAccount.UnableToCreateUserExists, Name));
                }
                try {
                    ua = new com.aurawin.core.storage.entities.domain.UserAccount(DomainId, Name);
                    ssn.save(ua); // get Id
                    tx.commit();
                    ssn.close();
                } catch (Exception e){
                    tx.rollback();
                    ssn.close();
                }
                entityCreated(entities, ua);
                return ua;
            }
            public static com.aurawin.core.storage.entities.domain.UserAccount Lookup(Entities entities,long DomainId, long Id) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.UserAccount ua;
                Transaction tx = ssn.beginTransaction();
                q = Database.Query.Domain.UserAccount.ById.Create(ssn, DomainId, Id);
                return (com.aurawin.core.storage.entities.domain.UserAccount) q.uniqueResult();
            }
            public void Delete(Entities entities, com.aurawin.core.storage.entities.domain.UserAccount Account) throws Exception {
                Session ssn = entities.Sessions.openSession();
                Transaction tx = ssn.beginTransaction();
                try {
                    ssn.delete(Account);
                    tx.commit();
                    ssn.close();
                } catch (Exception e) {
                    tx.rollback();
                    ssn.close();
                }
                entityDeleted(entities, Account);
            }
        }
        public static class Avatar{
            public static com.aurawin.core.storage.entities.domain.Avatar Create(Entities entities, com.aurawin.core.storage.entities.domain.UserAccount ua) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.Avatar a;
                q = Database.Query.Domain.Avatar.ByOwnerAndKind.Create(ssn, ua.getDomainId(), ua.getId(),Namespace.Entities.Domain.UserAccount.Avatar.getId());
                a = (com.aurawin.core.storage.entities.domain.Avatar) q.uniqueResult();
                if (a != null) {
                    throw new Exception(Table.String(Table.Exception.Entities.Domain.Avatar.UnableToCreateAvatarExists));
                }
                Transaction tx = ssn.beginTransaction();
                try {
                    a = new com.aurawin.core.storage.entities.domain.Avatar(ua.getDomainId(),ua.getId(),Namespace.Entities.Domain.UserAccount.Avatar.getId());
                    ssn.save(a); // get Id
                    ua.setAvatarId(a.getId());
                    ssn.update(ua);
                    tx.commit();
                    ssn.close();
                } catch (Exception e){
                    tx.rollback();
                    ssn.close();
                }
                entityCreated(entities, a);
                return a;
            }
            public static com.aurawin.core.storage.entities.domain.Avatar Create(Entities entities, com.aurawin.core.storage.entities.domain.Roster r) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.Avatar a = null;
                Transaction tx = ssn.beginTransaction();
                try {
                    a = new com.aurawin.core.storage.entities.domain.Avatar(r.getDomainId(),r.getOwnerId(),Namespace.Entities.Domain.Roster.Avatar.getId());
                    ssn.save(a);
                    r.setAvatarId(a.getId());
                    ssn.update(r);

                    tx.commit();
                    ssn.close();
                    entityCreated(entities, a);
                    return a;
                } catch (Exception e){
                    tx.rollback();
                    ssn.close();
                }
                return null;
            }
        }
    }
}
