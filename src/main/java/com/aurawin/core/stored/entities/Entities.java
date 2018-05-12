package com.aurawin.core.stored.entities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.*;
import com.aurawin.core.stored.entities.loader.Loader;
import com.aurawin.core.stored.entities.loader.Result;

import com.aurawin.core.stored.parameter.Parameters;
import org.hibernate.HibernateException;
import org.hibernate.Session;


import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.query.Query;


public class Entities {

    public static final boolean UseCurrentTransaction = true;
    public static final boolean UseNewTransaction = false;
    public static final boolean CascadeOn = true;
    public static final boolean CascadeOff = false;
    public static boolean Loaded = false;
    private static Loader Loader;
    private static Manifest Owner;
    private static SessionFactory Factory;

    public static boolean Initialize(Manifest manifest){
        Loaded = false;
        Loader = new Loader();
        Owner=manifest;
        if (Factory==null) {
            Factory = Hibernate.openSession(manifest);
            if (Loader.Cache.size()>0) {
                ClassLoader cL = Loader.Cache.get(0);
                Thread.currentThread().setContextClassLoader(cL);
            }
            Loaded=true;
            return true;
        } else {
            return RecreateFactory();
        }

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
    public static boolean RecreateFactory(){
        if ((Factory!=null) && (Factory.isClosed()==false)) {
            Factory.close();
        }
        if (Loader.Cache.size()>0) {
            ClassLoader cL = Loader.Cache.get(0);
            Thread.currentThread().setContextClassLoader(cL);
        }
        try{
            Factory = Hibernate.openSession(Owner);
            Loader.Injected=false;
            Loaded=true;

            return true;
        } catch (Exception ex){
            return false;

        }
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
                    if ((m!=null)  && (m.getName())=="entityCreated") {
                        try {
                            m.invoke(obj, obj, Cascade);
                        } catch (Exception err){
                            Syslog.Append(goe.getCanonicalName(),m.getName(), Table.Format(Table.Exception.Entities.EntityNotifyExecution,err.getMessage()));
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void entityPurge(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        EntityDispatch ed = obj.getClass().getAnnotation(EntityDispatch.class);
        if ((ed != null) && (ed.onPurge() == true)) {
            Method[] methods = null;
            Iterator it = Owner.Annotated.iterator();
            while (it.hasNext()){
                Class<?> goe = (Class<?>) it.next();
                if (Stored.class.isAssignableFrom(obj.getClass())) {
                    methods = goe.getDeclaredMethods();
                    for (Method m : methods) {
                        if ((m != null) && (m.getName().equalsIgnoreCase("entityPurge"))) {
                            try {
                                m.invoke(obj, obj, Cascade);
                            } catch (Exception err) {
                                Syslog.Append(goe.getCanonicalName(), m.getName(), Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                            }
                            break;
                        }
                    }
                }

            }
        }


    }
    private static void entityDeleted(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        EntityDispatch ed = obj.getClass().getAnnotation(EntityDispatch.class);
        if ((ed != null) && (ed.onDeleted() == true)) {

            Method[] methods = null;
            Iterator it = Owner.Annotated.iterator();
            while (it.hasNext()) {
                Class<?> goe = (Class<?>) it.next();
                methods = goe.getMethods();
                for (Method m : methods) {
                    if ((m != null) && (m.getName().equalsIgnoreCase("entityDeleted"))) {
                        try {
                            m.invoke(obj, obj, Cascade);
                        } catch (Exception err) {
                            Syslog.Append(goe.getCanonicalName(), m.getName(), Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                        }
                        break;
                    }
                }

            }
        }
    }
    private static void entityUpdated(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        EntityDispatch ed = obj.getClass().getAnnotation(EntityDispatch.class);
        if ((ed != null) && (ed.onDeleted() == true)) {
            Method[] methods = null;
            Iterator it = Owner.Annotated.iterator();
            while (it.hasNext()) {
                Class<?> goe = (Class<?>) it.next();

                methods = goe.getMethods();
                for (Method m : methods) {
                    if ((m != null) && (m.getName().equalsIgnoreCase("entityUpdated"))) {
                        try {
                            m.invoke(obj, obj, Cascade);
                        } catch (Exception err) {
                            Syslog.Append(goe.getCanonicalName(), m.getName(), Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                        }
                        break;
                    }
                }

            }
        }
    }
    public static ArrayList<Stored> toArrayList(List Items){
        ArrayList<Stored> al = new ArrayList<>();
        for (Object o : Items){
            if (o instanceof Stored){
                Stored s = (Stored) o;
                al.add(s);
            }
        }
        return al;
    }
    public static boolean Save(Stored e, boolean Cascade)

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
            Syslog.Append(Entities.class.getCanonicalName(),"Save", Table.Format(Table.Exception.Entities.EntityNotifyExecution,err.getMessage()));
            return false;
        }
        if (Cascade==true) {
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onCreated() == true)) {
                try {
                    entityCreated(e, Cascade);
                } catch (Exception err) {
                    Syslog.Append(Entities.class.getCanonicalName(),"Save.entityCreated", Table.Format(Table.Exception.Entities.EntityNotifyExecution,err.getMessage()));
                }
            }
        }
        return true;

    }
    public static boolean Update(Stored e,boolean Cascade)
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
            Syslog.Append(Entities.class.getCanonicalName(),"Update", Table.Format(Table.Exception.Entities.EntityNotifyExecution,err.getMessage()));
            return false;
        }
        if (Cascade==true) {
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onUpdated() == true)) {
                try {
                    entityUpdated(e, Cascade);
                } catch (Exception err) {
                    Syslog.Append(Entities.class.getCanonicalName(),"Update.entityUpdated", Table.Format(Table.Exception.Entities.EntityNotifyExecution,err.getMessage()));
                }
            }
        }
        return true;
    }
    public static boolean Purge(Stored e, boolean Cascade){
        EntityDispatch ed;
        if (Cascade==true) {
            ed = e.getClass().getAnnotation(EntityDispatch.class);
            try {
                if ((ed != null) && (ed.onPurge() == true)) {
                    entityPurge(e, Cascade);
                    return true;
                }
            } catch (Exception err) {
                Syslog.Append(Entities.class.getCanonicalName(), "Purge.entityPurge", Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
            }
        }
        return false;

    }
    public static boolean Delete(Stored e,boolean Cascade, boolean useSessionTransaction)
    {
        EntityDispatch ed;
        Session s = (useSessionTransaction) ?  Factory.getCurrentSession() : openSession();
        try {
            Transaction tx = (useSessionTransaction) ? s.getTransaction() : s.beginTransaction();
            try {
                s.delete(e);
                if (!useSessionTransaction) {
                    try {
                        tx.commit();
                    } catch (Exception err) {
                        tx.rollback();
                        Syslog.Append(e.getClass().getCanonicalName(), "Delete.Commit", Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                        return false;
                    }
                }
            } catch (Exception err) {
                Syslog.Append(e.getClass().getCanonicalName(), "Delete", Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                return false;
            }
            if (Cascade == true) {
                ed = e.getClass().getAnnotation(EntityDispatch.class);
                try {
                    if ((ed != null) && (ed.onDeleted() == true)) {
                        entityDeleted(e, Cascade);
                    }
                } catch (Exception err) {
                    Syslog.Append(Entities.class.getCanonicalName(), "Delete.entityDeleted", Table.Format(Table.Exception.Entities.EntityNotifyExecution, err.getMessage()));
                }
            }
            return true;
        } finally{
            if (!useSessionTransaction) s.close();
        }
    }
    public static void Identify(Stored e){
        Session s = openSession();
        try{
            e.Identify(s);
        } finally {
            s.close();
        }
    }
    public static void Identify(ArrayList<UniqueId> es){
        es.stream().forEach(e-> Identify(e));
    }
    @SuppressWarnings("unchecked")
    public static <T extends Stored>T Lookup(Class<? extends Stored> CofE, String Name) {
        Session ssn = acquireSession();
        if (ssn==null) return null;
        try {
            QueryByName qc = CofE.getAnnotation(QueryByName.class);
            Query q = ssn.getNamedQuery(qc.Name());
            if (q!=null) {
                for (String sF : qc.Fields()) {
                    q.setParameter(sF, Name);
                }
                return (T) CofE.cast(q.uniqueResult());
            } else {
                return null;
            }
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
                Query q = ssn.getNamedQuery(qc.Name());
                if (q!=null) {
                    q.setParameter("Id", Id);
                    Object o = q.uniqueResult();
                    return (o == null) ? null : (T) CofE.cast(o);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            ssn.close();
        }
    }

    public static ArrayList<Stored> Lookup(QueryByOwnerId aQuery, long Id){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name());
            if (q != null) {
                try {
                    q.setParameter("OwnerId", Id);
                    return toArrayList(q.list());
                } catch (Exception ex){
                    Syslog.Append("Entities.Lookup","QueryByOwnerId.setParameter", Table.Format(Table.Exception.Entities.EntityNotifyExecution,ex.getMessage()));
                    return new ArrayList<Stored>();
                }

            } else {
                return new ArrayList<Stored>();
            }
        } catch (Exception ex){
            Syslog.Append("Entities.Lookup","QueryByOwnerId", Table.Format(Table.Exception.Entities.EntityNotifyExecution,ex.getMessage()));
            return new ArrayList<Stored>();
        } finally{
            ssn.close();
        }
    }
    public static ArrayList<Stored> Lookup(QueryAll aQuery){
        Session ssn = acquireSession();
        if (ssn==null) return new ArrayList<Stored>();
        try {
            return toArrayList(ssn.getNamedQuery(aQuery.Name()).list());
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
            ssn.load(item, item.getId());
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
    @SuppressWarnings("unchecked")
    public static ArrayList<Stored> Fetch(Class<? extends Stored> CofE,String NamedQuery, Parameters Params){
        Session ssn = acquireSession();
        try{
            Query q = ssn.getNamedQuery(NamedQuery);


            Params.stream().forEach(p -> q.setParameter(p.Key,p.Value));
            return (ArrayList<Stored>) q.list().stream()
                    .filter(s -> s instanceof Stored)
                    .map(CofE::cast)
                    .collect(Collectors.toCollection(ArrayList::new));


        } finally {
            ssn.close();
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

