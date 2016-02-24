package com.aurawin.core.plugin;

import com.aurawin.core.stored.Manifest;
import com.aurawin.core.plugin.Plugin;
import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    ArrayList<Plugin> Items;

    public Plugins(Session ssn) {
        Items = new ArrayList<Plugin>();

        Plugin p = new Noid();
        Discover(ssn,p);
    }

    public void Discover(Session ssn, Plugin plugin) {
        if (plugin.Header.Annotation==null) {
            plugin.Header.Annotation = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);
        }
        if (plugin.Header.getId() == 0){
            plugin.Header.setNamespace(plugin.Header.Annotation.Namespace());
        }
        Plugin p = getPlugin(plugin.Header.Annotation.Namespace());
        if (p==null) {
            plugin.Setup(ssn);
            Items.add(p);
        }
    }
    public Plugin getPlugin(String Namespace){
        for (Plugin p : Items) {
            if (p.Header.Annotation.Namespace()==Namespace)
                return p;
        }
        return null;
    }
}
