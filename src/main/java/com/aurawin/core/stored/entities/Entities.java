package com.aurawin.core.stored.entities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aurawin.core.lang.Table;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.*;
import com.aurawin.core.stored.entities.loader.Loader;
import com.aurawin.core.stored.entities.loader.Result;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Entities {
    public static final boolean CascadeOn = true;
    public static final boolean CascadeOff = false;

    private Loader L;

    public Manifest Owner;
    public SessionFactory Sessions;

    public Entities(Manifest manifest) {
        L = new Loader();
        Owner=manifest;
        Sessions=Hibernate.openSession(manifest);
        RecreateFactory();
    }
    public Result Install(String Namespace){
        Result r = L.Check(this,Namespace);
        if (Stored.class.isAssignableFrom(r.Class)){
            if (Owner.Annotated.indexOf(r.Class)==-1){
                Owner.Annotated.add(r.Class);
            }
        }
        return r;
    }
    public Loader getLoader(){
        return L;
    }
    public void RecreateFactory(){
        if ((Sessions!=null) && (Sessions.isClosed()==false))
          Sessions.close();
        if (L.Cache.size()>0) {
            ClassLoader cL = L.Cache.get(0);
            Thread.currentThread().setContextClassLoader(cL);
        }
        Sessions = Hibernate.openSession(Owner);
        L.Injected=false;
    }
    public boolean hasInjected(){
        return (L.Injected==true);
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
        Session ssn = Sessions.openSession();
        try {
            Transaction tx = ssn.beginTransaction();
            try {
                ssn.save(e);
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                return false;
            }
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onCreated() == true)) {
                entityCreated( e);
            }
            return true;
        } finally{
            ssn.close();
        }
    }
    public boolean  Update(Stored e,boolean Cascade)
            throws InvocationTargetException,NoSuchMethodException, IllegalAccessException
    {
        Session ssn = Sessions.openSession();
        try {
            Transaction tx = ssn.beginTransaction();
            try {
                ssn.update(e);
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                return false;
            }
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onUpdated() == true)) {
                entityUpdated(e, Cascade);
            }
            return true;
        } finally{
            ssn.close();
        }
    }
    public boolean  Delete(Stored e,boolean Cascade)
            throws InvocationTargetException, NoSuchMethodException,IllegalAccessException
    {
        Session ssn = Sessions.openSession();
        try {
            Transaction tx = ssn.beginTransaction();
            try {
                ssn.delete(e);
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                return false;
            }
            EntityDispatch ed = e.getClass().getAnnotation(EntityDispatch.class);
            if ((ed != null) && (ed.onDeleted() == true)) {
                entityDeleted(e, Cascade);
            }
            return true;
        } finally{
            ssn.close();
        }
    }
    public Stored Lookup(Class<? extends Stored> CofE, String Name) {
        Session ssn = Sessions.openSession();
        try {
            QueryByName qc = CofE.getAnnotation(QueryByName.class);
            Query q = ssn.getNamedQuery(qc.Name());
            for (String sF : qc.Fields()){
                q.setString(sF, Name);
            }
            return CofE.cast(q.uniqueResult());
        } finally{
            ssn.close();
        }
    }
    public Stored Lookup(Class<? extends Stored> CofE,long DomainId, long Id){
        Session ssn = Sessions.openSession();
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
    public Stored Lookup(Class<? extends Stored> CofE,long Id) {
        Session ssn = Sessions.openSession();
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
    public List<Stored> Lookup(QueryByDomainId aQuery, long Id){
        Session ssn = Sessions.openSession();
        try {
            Query q = ssn.getNamedQuery(aQuery.Name())
                    .setLong("DomainId",Id);
            if (q!=null){
                return new ArrayList(q.list());
            } else {
                return null;
            }
        } finally{
            ssn.close();
        }
    }
    public boolean Fetch(Stored e)
            throws NoSuchFieldException,IllegalAccessException,Exception
    {
        Session ssn = Sessions.openSession();
        try {
            ssn.load(e,new Long(e.getId()));
            FetchFields fl = e.getClass().getAnnotation(FetchFields.class);
            if (fl!=null) {
                for (FetchField fld : fl.value() ) {
                    Field f = e.getClass().getDeclaredField(fld.Target());
                    f.setAccessible(true);
                    Object val = f.get(e);
                    org.hibernate.Hibernate.initialize(val);
                }
                return true;
            } else {
                throw new Exception(Table.Format(Table.Exception.Entities.EntityAnnotationForFetchNotDefined,e.getClass().getCanonicalName()));
            }
        } finally{
            ssn.close();
        }
    }

}

