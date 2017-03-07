package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.stored.entities.UniqueId;
import org.hibernate.Session;

import java.util.HashMap;

import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.plugin.annotations.Command;

public abstract class Plug extends UniqueId implements Methods {
    public String Namespace;
    public Plugin Annotation;
    public HashMap<String,CommandInfo>Commands;
    public Plug() {
        Commands = new HashMap<>();
    }
    public PluginState Setup(Session ssn){
        PluginState r = PluginState.PluginFailure;
        Annotation=getClass().getAnnotation(Plugin.class);
        if (Annotation!=null) {
            setNamespace(
                    com.aurawin.core.lang.Namespace.Entities.Plugin.getNamespace(
                            Annotation.Package(),
                            Annotation.Name()
                    )
            );
            Identify(ssn);
            HashMap<String, CommandInfo> ms = new HashMap<>();
            java.lang.reflect.Method[] fs = getClass().getMethods();
            Command aC = null;
            for (java.lang.reflect.Method f : fs) {
                aC = f.getAnnotation(Command.class);
                if (aC != null) {
                    String ns=com.aurawin.core.lang.Namespace.Entities.Plugin.getMethodNamespace(
                            Annotation.Package(),
                            Annotation.Name(),
                            aC.Name()
                    );
                    CommandInfo ci = new CommandInfo();
                    ci.setNamespace(ns);
                    ci.Identify(ssn);
                    ci.Method=f;
                    ci.annotationPlugin=Annotation;
                    ci.annotationCommand=aC;
                    ci.Plugin=this;
                    ms.put(aC.Namespace(),ci);
                }
            }
            Commands = ms;
            r =  PluginState.PluginSuccess;
        } else {
            r = PluginState.PluginAnnotationError;
        }
        return r;
    }
    @Override
    public PluginState Execute(Session ssn, String Namespace, Item itm) {
        CommandInfo ci = Commands.get(Namespace);
        if (ci!=null) {
            try {
                return (PluginState) ci.Method.invoke(this,ssn,itm);
            } catch (Exception e) {
                return PluginState.PluginException;
            }
        }
        return PluginState.PluginMethodNotFound;
    }
}
