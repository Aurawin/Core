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
import org.hibernate.query.Query;


public class Entities {
    public static final boolean CascadeOn = true;
    public static final boolean CascadeOff = false;

    private Loader loader_;


    public Manifest Owner;
    public SessionFactory Factory;

    public Entities(Manifest manifest) {
        loader_ = new Loader();

        Owner=manifest;
        Factory=Hibernate.openSession(manifest);
        RecreateFactory();
    }
    public Result Install(String Namespace){
        Result r = loader_.Check(this,Namespace);
        if (Stored.class.isAssignableFrom(r.Class)){
            if (Owner.Annotated.indexOf(r.Class)==-1){
                Owner.Annotated.add(r.Class);
            }
        }
        return r;
    }
    public Loader getLoader(){
        return loader_;
    }
    public void RecreateFactory(){
        if ((Factory!=null) && (Factory.isClosed()==false)) {
            Factory.close();
        }
        if (loader_.Cache.size()>0) {
            ClassLoader cL = loader_.Cache.get(0);
            Thread.currentThread().setContextClassLoader(cL);
        }
        Factory = Hibernate.openSession(Owner);
        loader_.Injected=false;
    }
    public boolean hasInjected(){
        return (loader_.Injected==true);
    }

    public Session acquireSession(){
        Session s = null;
        try {
            s = Factory.getCurrentSession();
        } catch (HibernateException e) {
            s = Factory.openSession();
            s.beginTransaction();
        }
        return s;
    }
    private void entityCreated(Stored obj)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true){
                Method m = goe.getMethod("entityCreated",Entities.class,Stored.class);
	            if (m!=null) m.invoke(obj,this,obj);
            }
        }
    }
    private void entityDeleted(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
	        Class<?> goe = (Class<?>) it.next();
	        if (Stored.class.isAssignableFrom(goe)==true) {
		        Method m = goe.getMethod("entityDeleted", Entities.class, Stored.class, boolean.class);
		        if (m!=null) m.invoke(obj, this, obj, Cascade);
	        }
        }

    }
    private void entityUpdated(Stored obj, boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {

        Iterator it = Owner.Annotated.iterator();
        while (it.hasNext()){
            Class<?> goe = (Class<?>) it.next();
            if (Stored.class.isAssignableFrom(goe)==true) {
                Method m = goe.getMethod("entityUpdated", Entities.class, Stored.class, boolean.class);
                if (m!=null) m.invoke(obj, this, obj, Cascade);
            }
        }

    }
    public boolean Save(Stored e)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Session s = acquireSession();
        try {
            s.save(e);
            s.getTransaction().commit();
            s.close();

            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onCreated() == true)) {
                entityCreated(e);
            }
            return true;
        } catch (Exception err) {
            s.getTransaction().rollback();
        }

        return false;
    }
    public boolean Update(Stored e,boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Session s = acquireSession();
        try {
            s.update(e);
            s.getTransaction().commit();
            s.close();
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onUpdated() == true)) {
                entityUpdated(e, Cascade);
            }

            return true;

        } catch (Exception err) {
            s.getTransaction().rollback();
        }
        return false;
    }
    public boolean Delete(Stored e,boolean Cascade)
            throws InvocationTargetException, NoSuchMethodException,IllegalAccessException
    {
        Session s = acquireSession();
        try{
            s.delete(e);
            s.getTransaction().commit();
            s.close();

            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onDeleted() == true)) {
                entityDeleted(e, Cascade);
            }
            return true;

        } catch (Exception err) {
            s.getTransaction().rollback();
        }
        return false;

    }
    public <T extends Stored>T Lookup(Class<? extends Stored> CofE, String Name) {
        Session ssn = acquireSession();
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
    public <T extends Stored>T Lookup(Class<? extends Stored> CofE,long DomainId, long Id){
        Session ssn = acquireSession();
        try {
            QueryById qc = CofE.getAnnotation(QueryById.class);
            Query q = ssn.getNamedQuery(qc.Name())
                    .setParameter("DomainId", DomainId)
                    .setParameter("Id", Id);

            return (T) CofE.cast(q.uniqueResult());
        } finally {
            ssn.close();
        }
    }
    public <T extends Stored>T Lookup(Class<? extends Stored> CofE,long DomainId, String Name){
        Session ssn = acquireSession();
        try {
            QueryById qc = CofE.getAnnotation(QueryById.class);
            Query q = ssn.getNamedQuery(qc.Name())
                    .setParameter("DomainId", DomainId)
                    .setParameter("Name", Name);

            return (T) CofE.cast(q.uniqueResult());
        } finally {
            ssn.close();
        }
    }
    public <T extends Stored>T Lookup(Class<? extends Stored> CofE,long Id) {
        Session ssn = acquireSession();
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

    public ArrayList<Stored> Lookup(QueryByDomainId aQuery, long Id){
        Session ssn = acquireSession();
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
    public ArrayList<Stored> Lookup(QueryByOwnerId aQuery, long Id){
        Session ssn = acquireSession();
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
    public ArrayList<Stored> Lookup(QueryByNetworkId aQuery, long Id){
        Session ssn = acquireSession();
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
    public ArrayList<Stored> Lookup(QueryByFileId aQuery, long Id){
        Session ssn = acquireSession();
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
    public ArrayList<Stored> Lookup(QueryByFolderId aQuery, long Id){
        Session ssn = acquireSession();
        try {
            return new ArrayList(ssn.getNamedQuery(aQuery.Name())
                    .setParameter("FolderId", Id)
                    .list()
            );
        } finally {
            ssn.close();
        }
    }
    public boolean Fetch(Stored e)
            throws Exception
    {
        Session ssn = acquireSession();
        try {
            ssn.load(e, new Long(e.getId()));
            FetchFields fl = e.getClass().getAnnotation(FetchFields.class);
            if (fl != null) {
                for (FetchField fld : fl.value()) {
                    Field f = e.getClass().getDeclaredField(fld.Target());
                    f.setAccessible(true);
                    Object val = f.get(e);
                    org.hibernate.Hibernate.initialize(val);
                }
                return true;
            } else {
                throw new Exception(Table.Format(Table.Exception.Entities.EntityAnnotationForFetchNotDefined, e.getClass().getCanonicalName()));
            }
        } finally {
            ssn.close();
        }
    }

}

