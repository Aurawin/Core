package com.aurawin.core.plugin;

import com.aurawin.core.stored.Manifest;
import com.aurawin.core.plugin.Plugin;
import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    volatile ArrayList<Plugin> Items;

    public Plugins() {
        Items = new ArrayList<Plugin>();
    }

    public void Install(Session ssn, Plugin plugin) {
        if (plugin.Header.Annotation==null) {
            plugin.Header.Annotation = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);
        }
        if (plugin.Header.getId() == 0){
            plugin.Header.setNamespace(plugin.Header.Annotation.Namespace());
        }
        Plugin p = getPlugin(plugin.Header.Annotation.Namespace());
        if (p==null) {
            plugin.Setup(ssn);
            Items.add(plugin);
        }
    }
    public void Uninstall(Session ssn, String Namespace){
        Plugin p = getPlugin(Namespace);
        if (p!=null){
            Items.remove(p);
            p.Teardown(ssn);
        }
    }
    public Plugin getPlugin(String Namespace){
        return Items.stream()
                .filter(p -> p.Header.getNamespace().equalsIgnoreCase(Namespace))
                .findFirst()
                .orElse(null);
    }
}
