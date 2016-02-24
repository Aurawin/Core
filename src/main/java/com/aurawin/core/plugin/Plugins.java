package com.aurawin.core.plugin;

import com.aurawin.core.stored.Manifest;
import com.aurawin.core.plugin.Plugin;
import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    ArrayList<Plugin> Items;

    public Plugins(org.hibernate.Session session) {
        Items = new ArrayList<Plugin>();
    }

    public void Discover(Manifest manifest,Session session, Plugin plugin) {
        if (plugin.Header.Annotation==null)
            plugin.Header.Annotation = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);
        /*
            if (plugin.Header.Id == 0)
                manifest.
        */

        Plugin p = getPlugin(plugin.Header.Annotation.Namespace());
        if (p==null) {
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
