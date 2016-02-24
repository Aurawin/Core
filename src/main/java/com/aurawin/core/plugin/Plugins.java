package com.aurawin.core.plugin;

import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    ArrayList<Plugin> Items;
    Session Session;

    public Plugins(org.hibernate.Session session) {
        Items = new ArrayList<Plugin>();
        Session = session;
    }

    public void Discover(Plugin plugin) {
        if (plugin.Header.Manifest==null)
            plugin.Header.Manifest = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);

        //if (plugin.Header.Id==0)

        Plugin p = getPlugin(plugin.Header.Manifest.Namespace());
        if (p==null) {
            Items.add(p);
        }
    }
    public Plugin getPlugin(String Namespace){
        for (Plugin p : Items) {
            if (p.Header.Manifest.Namespace()==Namespace)
                return p;
        }
        return null;
    }
}
