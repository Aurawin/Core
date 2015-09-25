package com.aurawin.core.storage.entities;

import java.lang.reflect.Method;
import java.util.Iterator;

import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.annotations.EntityDispatch;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Entities {
    public static final boolean CascadeOn = true;
    public static final boolean CascadeOff = false;
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

//        public static class Folder{
//            com.aurawin.core.storage.entities.domain.network.Folder Create(
//                    Entities entities,
//                    com.aurawin.core.storage.entities.domain.UserAccount ua,
//                    com.aurawin.core.storage.entities.domain.network.Network network,
//                    com.aurawin.core.storage.entities.domain.network.Folder Parent,
//                    String Name) {
//                com.aurawin.core.storage.entities.domain.network.Folder f = null;
//                String Path = "";
//                Path = (Parent == null) ? Name : Parent.getPath();
//
//                Session ssn = entities.Sessions.openSession();
//                Query q;
//                try {
//                    q = Database.Query.Domain.Folder.ByPath.Create(ssn, ua.getDomainId(), ua.getId(), network.getId(), Path);
//                    f = (com.aurawin.core.storage.entities.domain.network.Folder) q.uniqueResult();
//                    if (f != null) {
//                        throw new Exception(Table.String(Table.Exception.Entities.Domain.Folder.UnableToCreateExists));
//                    }
//                } catch (Exception e) {
//                    ssn.close();
//                    return null;
//                }
//                Transaction tx = ssn.beginTransaction();
//                try {
//                    if (Parent == null) {
//                        f = new com.aurawin.core.storage.entities.domain.network.Folder(ua.getDomainId(), ua.getId(), network.getId(), Name);
//                        network.Folders.add(f);
//                    } else {
//                        f = Parent.addChild(Name);
//                    }
//                    ssn.save(f);
//                    tx.commit();
//                    ssn.close();
//
//                    entityCreated(entities, f);
//
//                    return f;
//                } catch (Exception e) {
//                    tx.rollback();
//                    ssn.close();
//                    return null;
//                }
//
//            }
//        }
//
//    }
    public static boolean Create(Entities entities, Stored e){
        Session ssn = entities.Sessions.openSession();
        Transaction tx = ssn.beginTransaction();
        try {
            ssn.save(e);
            tx.commit();
            ssn.close();

            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed!=null) && (ed.onCreated()==true)) {
                entityCreated(entities, e);
            }

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

            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed!=null) && (ed.onUpdated()==true)) {
                entityUpdated(entities, e, Cascade);
            }

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

            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ( (ed!=null) && (ed.onDeleted()==true)) {
                entityDeleted(entities, e, Cascade);
            }

            return true;
        } catch (Exception ex) {
            tx.rollback();
            ssn.close();
            return false;
        }
    }

    public static Stored Lookup(Entities entities,long DomainId, long Id, Class<? extends Stored> CofE) throws Exception {
        Session ssn = entities.Sessions.openSession();
        try {
            QueryById qc = CofE.getAnnotation(QueryById.class);
            Query q = ssn.getNamedQuery(qc.Name())
                    .setLong("DomainId", DomainId)
                    .setLong("Id", Id);

            return CofE.cast(q.uniqueResult());
        } finally{
            ssn.close();
        }
    }
    public static Stored Lookup(Entities entities,long Id, Class<? extends Stored> CofE) throws Exception {
        Session ssn = entities.Sessions.openSession();
        try {
            QueryById qc = CofE.getAnnotation(QueryById.class);
            if (qc!=null) {
                Query q = ssn.getNamedQuery(qc.Name())
                        .setLong("Id", Id);
                return CofE.cast(q.uniqueResult());
            } else {
                return null;
            }
        } finally{
            ssn.close();
        }
    }
    public static boolean Fetch(Entities entities, Stored e) throws Exception{
        Session ssn = entities.Sessions.openSession();
        try {
            ssn.load(e,new Long(e.getId()));
            return true;
//            FieldLoader fl = e.getClass().getAnnotation(FieldLoader.class);
//            if (fl!=null) {
//                for (FieldLoaderDef fld : fl.value() ) {
//                    Field f = e.getClass().getDeclaredField(fld.Target());
//                    Object val = f.get(e);
//                    org.hibernate.Hibernate.initialize(val);
//                };
//
//                return true;
//            } else {
//                return false;
//            }
        } finally{
            ssn.close();
        }

    }
}

