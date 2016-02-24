package com.aurawin.core.plugin;

import com.aurawin.core.stored.Manifest;
import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    ArrayList<Plugin> Items;

    public Plugins(org.hibernate.Session session) {
        Items = new ArrayList<Plugin>();
    }

    public void Discover(Manifest manifest,Session session, Plugin plugin) {
        if (plugin.Header.Manifest==null)
            plugin.Header.Manifest = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);

        if (plugin.Header.Id==0)
            manifest.

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
