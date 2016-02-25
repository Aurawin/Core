package com.aurawin.core.plugin;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.stored.entities.UniqueId;
import org.hibernate.Session;

import java.lang.annotation.Annotation;
import com.aurawin.core.plugin.annotations.Command;

public abstract class Plugin implements Method {
    public Info Header;
    public Method [] Methods;
    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }

    protected long ID;

    public Plugin() {
        Header = new Info();
    }

    public MethodState Setup(Session ssn){
        Header.Verify(ssn);
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
                    u.Verify(ssn);
                }
            }
            return MethodState.msSuccess;
        } else {
            return MethodState.msFailure;
        }
    }
}
