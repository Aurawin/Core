package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

import java.util.HashMap;

import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.plugin.annotations.Command;

public abstract class Plug implements Methods {
    public PlugInfo Header;
    public HashMap<String,CommandInfo>Commands;
    public Plug() {
        Header = new PlugInfo();
        Commands = new HashMap<>();
    }
    public PluginState Setup(Session ssn){
        PluginState r = PluginState.PluginFailure;
        String ns = "";
        Plugin p = getClass().getAnnotation(Plugin.class);
        if (p!=null) {
            Header.Plugin = p;
            Header.Identify(ssn);
            HashMap<String, CommandInfo> ms = new HashMap<>();

            java.lang.reflect.Method[] fs = getClass().getMethods();
            Command aC = null;
            for (java.lang.reflect.Method f : fs) {
                aC = f.getAnnotation(Command.class);
                if (aC != null) {
                    ns=com.aurawin.core.lang.Namespace.Entities.Plugin.getMethodNamespace(
                            Header.Plugin.ClassName(),
                            Header.Plugin.Namespace(),
                            aC.Namespace()
                    );
                    CommandInfo ci = new CommandInfo();
                    ci.setNamespace(ns);
                    ci.Identify(ssn);
                    ci.Method=f;
                    ci.annotationPlugin=p;
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
