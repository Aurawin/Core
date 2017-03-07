package com.aurawin.core.stored.entities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.aurawin.core.lang.Table;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.*;
import com.aurawin.core.stored.entities.loader.Loader;
import com.aurawin.core.stored.entities.loader.Result;

import org.hibernate.HibernateException;
import org.hibernate.Session;


import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.query.Query;


public class Entities {
    public static final boolean CascadeOn = true;
    public static final boolean CascadeOff = false;

    private static Loader Loader;
    private static Manifest Owner;
    private static SessionFactory Factory;

    public static void Initialize(Manifest manifest){
        Loader = new Loader();
        Owner=manifest;
        if (Factory==null) {
            Factory = Hibernate.openSession(manifest);
            if (Loader.Cache.size()>0) {
                ClassLoader cL = Loader.Cache.get(0);
                Thread.currentThread().setContextClassLoader(cL);
            }
        } else {
            RecreateFactory();
        }
        Owner.Verify();
    }
    public static Session openSession(){
        return Factory.openSession();
    }
    @SuppressWarnings("unchecked")
    public static Result Install(String Namespace){
        Result r = Loader.Check(Namespace);
        if (Stored.class.isAssignableFrom(r.Class)){
            if (Owner.Annotated.indexOf(r.Class)==-1){
                Owner.Annotated.add(r.Class);
            }
        }
        return r;
    }
    public static Loader getLoader(){
        return Loader;
    }
    public static void RecreateFactory(){
        if ((Factory!=null) && (Factory.isClosed()==false)) {
            Factory.close();
        }
        if (Loader.Cache.size()>0) {
            ClassLoader cL = Loader.Cache.get(0);
            Thread.currentThread().setContextClassLoader(cL);
        }
        Factory = Hibernate.openSession(Owner);
        Loader.Injected=false;
    }
    public static boolean hasInjected(){
        return (Loader.Injected==true);
    }
    public static void removeAnnotatedClass(Class claz){
        Owner.Annotated.remove(claz);
    }
    public static Session acquireSession(){
        if (Factory==null) return null;
        Session s = null;
        try {
            s = Factory.getCurrentSession();
        } catch (HibernateException e) {
            s = Factory.openSession();
            s.beginTransaction();
        }
        return s;
    }
    private static void entityCreated(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Method[] methods = null;
        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true){
                methods = goe.getMethods();
                for (Method m : methods){
                    if (m.getName()=="entityCreated") {
                        if (m!=null) m.invoke(obj, obj, Cascade);
                        break;
                    }
                }
            }
        }
    }
    private static void entityDeleted(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Method[] methods = null;
        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
	        Class<?> goe = (Class<?>) it.next();
	        if (Stored.class.isAssignableFrom(goe)==true) {
                methods = goe.getMethods();
                for (Method m : methods){
                    if (m.getName()=="entityDeleted") {
                        if (m!=null) m.invoke(obj, obj, Cascade);
                        break;
                    }
                }
	        }
        }

    }
    private static void entityUpdated(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Method[] methods = null;
        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true) {
                methods = goe.getMethods();
                for (Method m : methods){
                    if (m.getName()=="entityUpdated") {
                        if (m!=null) m.invoke(obj, obj, Cascade);
                        break;
                    }
                }
            }
        }

    }
    public static boolean Save(Stored e, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.save(e);
            tx.commit();
            s.close();
        } catch (Exception err) {
            tx.rollback();
            s.close();
            return false;
        }
        if (Cascade==true) {
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onCreated() == true)) {
                try {
                    entityCreated(e, Cascade);
                } catch (Exception err) {

                }
            }
        }
        return true;

    }
    public static boolean Update(Stored e,boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.update(e);
            tx.commit();
            s.close();
        } catch (Exception err) {
            tx.rollback();
            s.close();
            return false;
        }
        if (Cascade==true) {
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onUpdated() == true)) {
                try {
                    entityUpdated(e, Cascade);
                } catch (Exception err) {

                }
            }
        }
        return true;
    }
    public static boolean Delete(Stored e,boolean Cascade)
            throws InvocationTargetException, NoSuchMethodException,IllegalAccessException
    {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try{
            s.delete(e);
            tx.commit();
            s.close();
        } catch (Exception err) {
            tx.rollback();
            s.close();
            return false;
        }
        if (Cascade==true) {
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            try {
                if ((ed != null) && (ed.onDeleted() == true)) {
                    entityDeleted(e, Cascade);
                }
            } catch (Exception err) {

            }
        }
        return true;
    }
    public static void Identify(Stored e){
        Session s = openSession();
        try{
            e.Identify(s);
        } finally {
            s.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Stored>T Lookup(Class<? extends Stored> CofE, String Name) {
        Session ssn = acquireSession();
        if (ssn==null) return null;
        try {
            QueryByName qc = CofE.getAnnotation(QueryByName.class);
            Query q = ssn.getNamedQuery(qc.Name());
            for (String sF : qc.Fields()) {
                q.setParameter(sF, Name);
            }
            return (T) CofE.cast(q.uniqueResult());
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Stored>T Lookup(Class<? extends Stored> CofE,long DomainId, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return null;
        try {
            QueryByDomainIdAndId qc = CofE.getAnnotation(QueryByDomainIdAndId.class);
            Query q = ssn.getNamedQuery(qc.Name())
                    .setParameter("DomainId", DomainId)
                    .setParameter("Id", Id);

            return (T) CofE.cast(q.uniqueResult());
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Stored>T Lookup(Class<? extends Stored> CofE,long DomainId, String Name){
        Session ssn = acquireSession();
        if (ssn==null) return null;
        try {
            QueryByDomainIdAndName qc = CofE.getAnnotation(QueryByDomainIdAndName.class);
            Query q = ssn.getNamedQuery(qc.Name())
                    .setParameter("DomainId", DomainId)
                    .setParameter("Name", Name);

            Stored r = (Stored) q.uniqueResult();
            return (T) CofE.cast(r);
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Stored>T Lookup(Class<? extends Stored> CofE,long Id) {
        Session ssn = acquireSession();
        if (ssn==null) return null;
        try {
            QueryById qc = CofE.getAnnotation(QueryById.class);
            if (qc != null) {
                Query q = ssn.getNamedQuery(qc.Name())
                        .setParameter("Id", Id);
                Object o = q.uniqueResult();
                return (o == null) ? null : (T) CofE.cast(o);
            } else {
                return null;
            }
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryByDomainId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name())
                    .setParameter("DomainId", Id);
            if (q != null) {
                return new ArrayList(q.list());
            } else {
                return null;
            }
        } finally{
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryByOwnerId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name())
                    .setParameter("OwnerId", Id);
            if (q != null) {
                return new ArrayList(q.list());
            } else {
                return null;
            }
        } finally{
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryByNetworkId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name())
                    .setParameter("NetworkId", Id);
            if (q != null) {
                return new ArrayList(q.list());
            } else {
                return null;
            }
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryByFileId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name())
                    .setParameter("FileId", Id);
            if (q != null) {
                return new ArrayList(q.list());
            } else {
                return null;
            }
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryByFolderId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            return new ArrayList(ssn.getNamedQuery(aQuery.Name())
                    .setParameter("FolderId", Id)
                    .list()
            );
        } finally {
            ssn.close();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Lookup(QueryAll aQuery){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            return new ArrayList(ssn.getNamedQuery(aQuery.Name()).list());
        } finally {
            ssn.close();
        }
    }
    private static void initializeFetchField(Session ssn,Stored item, Field field, FetchKind Kind, int Depth) throws
            IllegalAccessException,NoSuchFieldException
    {
        field.setAccessible(true);
        Object val = field.get(item);

        if (val instanceof PersistentBag) {
            PersistentBag pb = (PersistentBag) val;
            for (Object o: pb) {
                if (o instanceof Stored) {
                    Stored so = (Stored) o;
                    initializeFetchFields(ssn,so,Kind,Depth);
                } else {
                    org.hibernate.Hibernate.initialize(o);
                }
            }
        } else {
            if (val instanceof Stored){
                Stored so = (Stored) val;
                initializeFetchFields(ssn,so,Kind,Depth);
            } else {
                org.hibernate.Hibernate.initialize(val);
            }
        }
    }
    private static void initializeFetchFields(Session ssn, Stored item, FetchKind Kind, int Depth) throws
            NoSuchFieldException, IllegalAccessException
    {
        try {
            ssn.load(item, new Long(item.getId()));
        } catch (Exception e){

        }
        org.hibernate.Hibernate.initialize(item);
        Depth++;
        if ( (Kind==FetchKind.Infinite) ||  (Depth<=Kind.getValue()) ) {
            FetchFields fl = item.getClass().getAnnotation(FetchFields.class);
            if (fl != null) {
                for (FetchField fld : fl.value()) {
                    Field f = item.getClass().getDeclaredField(fld.Target());
                    if (f != null) initializeFetchField(ssn, item, f, Kind, Depth);
                }

            }
        }
    }
    public static boolean Fetch(Stored e, FetchKind Kind)
            throws Exception
    {
        int Depth = 0;
        Session ssn = acquireSession();
        if (ssn==null) return false;
        try {
            initializeFetchFields(ssn,e,Kind,Depth);
            return true;
        } finally {
            ssn.close();
        }
    }

}

