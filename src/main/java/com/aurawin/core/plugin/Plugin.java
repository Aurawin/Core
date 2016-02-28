package com.aurawin.core.plugin;

import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.stored.entities.UniqueId;
import org.hibernate.Session;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.aurawin.core.plugin.annotations.Command;

public abstract class Plugin implements Method {
    public Info Header;
    public KeyPair Methods;
    public Plugin() {
        Header = new Info();
        Methods = new KeyPair();
    }

    public MethodState Setup(Session ssn){
        Header.Identify(ssn);
        Methods.Invalidate();
        MethodState r = MethodState.msSuccess;
        if (Header.Annotation!=null) {
            java.lang.reflect.Method[] fs = getClass().getDeclaredMethods();
            for (java.lang.reflect.Method f : fs){
                Annotation a = f.getAnnotation(Command.class);
                if (a!=null) {
                    UniqueId u = new UniqueId(com.aurawin.core.lang.Namespace.Entities.Plugin.getNamespace(
                            Header.Annotation.Domain(),
                            Header.Annotation.ClassName(),
                            Header.Annotation.Namespace(),
                            ((Command) a).Namespace()
                    ));
                    u.Identify(ssn);
                    Methods.Update( ((Command) a).Namespace(),f);
                }
            }
        } else {
            r = MethodState.msFailure;
        }
        Methods.Purge();
        return r;
    }
    public java.lang.reflect.Method getMethod(String Namespace){
        Object o  = Methods.getData(Namespace);
        if (o instanceof java.lang.reflect.Method) {
            return (java.lang.reflect.Method) o;
        } else {
            return null;
        }
    }
    private Field getField(Field fs[], String Name){
        for (Field f:fs){
            if (f.getName().compareTo(Name)==0) return f;
        }
        return null;
    }
    @Override
    public MethodState Execute(Session ssn, String Namespace, Item itm) {
        java.lang.reflect.Method m = getMethod(Namespace);
        if (m!=null) {
            Command a = m.getAnnotation(Command.class);
            if (a != null) {
                if (a.Namespace().compareTo(Namespace) == 0) {
                    String[] P = a.Fields();
                    Object[] Fields = new Object[P.length];
                    Field[] fs = itm.getClass().getDeclaredFields();
                    for (int iLcv = 0; iLcv < Fields.length; iLcv++) {
                        Field f = getField(fs, P[iLcv]);
                        f.setAccessible(true);
                        try {
                            Fields[iLcv] = f.get(itm);
                        } catch (Exception e){
                            return MethodState.msNotFound;
                        }
                    }
                    try {
                        return (MethodState) m.invoke(this,ssn,itm,Fields);
                    } catch (Exception e) {
                        return MethodState.msException;
                    }
                }
            }
        }
        return MethodState.msNotFound;
    }
}
