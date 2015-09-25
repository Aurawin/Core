package com.aurawin.core.storage.entities;

import java.lang.reflect.Method;
import java.util.Iterator;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.entities.domain.Folder;
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
    private static void entityDeleted(Entities entities, Stored obj, boolean Cascade) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
	        Class<?> goe = (Class<?>) it.next();
	        if (Stored.class.isAssignableFrom(goe)==true) {
		        Method m = goe.getMethod("entityDeleted", Entities.class, Stored.class, boolean.class);
		        if (m!=null) m.invoke(obj, entities, obj, Cascade);
	        }
        }

    }
    private static void entityUpdated(Entities entities, Stored obj, boolean Cascade) throws Exception{
        Iterator it = entities.Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true) {
                Method m = goe.getMethod("entityUpdated", Entities.class, Stored.class, boolean.class);
                if (m!=null) m.invoke(obj, entities, obj, Cascade);
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
                throw new Exception(Table.Format(Table.Exception.Entities.Domain.UnableToCreateExists, Name));
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
                    throw new Exception(Table.Format(Table.Exception.Entities.Domain.UserAccount.UnableToCreateExists, Name));
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
        }
        public static class Avatar{
            public static com.aurawin.core.storage.entities.domain.Avatar Create(Entities entities, com.aurawin.core.storage.entities.domain.UserAccount ua) throws Exception{
                Session ssn = entities.Sessions.openSession();
                Query q;
                com.aurawin.core.storage.entities.domain.Avatar a;
                q = Database.Query.Domain.Avatar.ByOwnerAndKind.Create(ssn, ua.getDomainId(), ua.getId(),Namespace.Entities.Domain.UserAccount.Avatar.getId());
                a = (com.aurawin.core.storage.entities.domain.Avatar) q.uniqueResult();
                if (a != null) {
                    throw new Exception(Table.String(Table.Exception.Entities.Domain.Avatar.UnableToCreateExists));
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
        public static class Folder{
            com.aurawin.core.storage.entities.domain.Folder Create(
                    Entities entities,
                    com.aurawin.core.storage.entities.domain.UserAccount ua,
                    com.aurawin.core.storage.entities.domain.network.Network network,
                    com.aurawin.core.storage.entities.domain.Folder Parent,
                    String Name) {
                com.aurawin.core.storage.entities.domain.Folder f = null;
                String Path = "";
                Path = (Parent == null) ? Name : Parent.getPath();
                Session ssn = entities.Sessions.openSession();
                Query q;
                try {
                    q = Database.Query.Domain.Folder.ByPath.Create(ssn, ua.getDomainId(), ua.getId(), network.getId(), Path);
                    f = (com.aurawin.core.storage.entities.domain.Folder) q.uniqueResult();
                    if (f != null) {
                        throw new Exception(Table.String(Table.Exception.Entities.Domain.Folder.UnableToCreateExists));
                    }
                } catch (Exception e) {
                    ssn.close();
                    return null;
                }
                Transaction tx = ssn.beginTransaction();
                try {
                    if (Parent == null) {
                        f = new com.aurawin.core.storage.entities.domain.Folder(ua.getDomainId(), ua.getId(), network.getId(), Name);
                        network.Folders.add(f);
                    } else {
                        f = Parent.addChild(Name);
                    }
                    ssn.save(f);
                    tx.commit();
                    ssn.close();

                    entityCreated(entities, f);

                    return f;
                } catch (Exception e) {
                    tx.rollback();
                    ssn.close();
                    return null;
                }

            }
        }

    }
    public static boolean Create(Entities entities, Stored e){
        Session ssn = entities.Sessions.openSession();
        Transaction tx = ssn.beginTransaction();
        try {
            ssn.save(e);
            tx.commit();
            ssn.close();

            entityCreated(entities, e);

            return true;
        } catch (Exception ex){
            tx.rollback();
            ssn.close();
            return false;
        }
    }
    public static boolean  Update(
            Entities entities,
            Stored e,
            boolean Cascade
    ) {
        Session ssn = entities.Sessions.openSession();
        Transaction tx = ssn.beginTransaction();
        try {
            ssn.update(e);
            tx.commit();
            ssn.close();

            entityUpdated(entities, e, Cascade);

            return true;
        } catch (Exception ex) {
            tx.rollback();
            ssn.close();
            return false;
        }
    }
    public static boolean  Delete(
            Entities entities,
            Stored e,
            boolean Cascade
    ) {
        Session ssn = entities.Sessions.openSession();
        Transaction tx = ssn.beginTransaction();
        try {
            ssn.delete(e);
            tx.commit();
            ssn.close();

            entityDeleted(entities, e, Cascade);

            return true;
        } catch (Exception ex) {
            tx.rollback();
            ssn.close();
            return false;
        }
    }

    public static Stored Lookup(Entities entities,long DomainId, long Id, Class<? extends Stored> CofE) throws Exception {
        Session ssn = entities.Sessions.openSession();
        Query q;
        Stored e = CofE.newInstance();
        Transaction tx = ssn.beginTransaction();
        try {
            ssn.save(e);
            tx.commit();
            ssn.close();

            entityCreated(entities,e);

            return e;
        } catch (Exception ex){
            tx.rollback();
            ssn.close();
            return null;
        }
    }
}

