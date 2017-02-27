package com.aurawin.core.plugin;

import org.hibernate.Session;

import java.util.ArrayList;

public class Plugins {
    volatile ArrayList<Plug> Items;

    public Plugins() {
        Items = new ArrayList<Plug>();
    }

    public void Install(Session ssn, Plug plugin) {
        if (plugin.Header.Plugin==null) {
            plugin.Header.Plugin = plugin.getClass().getAnnotation(com.aurawin.core.plugin.annotations.Plugin.class);
        }
        if (plugin.Header.getId() == 0){
            plugin.Header.setNamespace(plugin.Header.Plugin.Namespace());
        }
        Plug p = getPlugin(plugin.Header.Plugin.Namespace());
        if (p==null) {
            plugin.Setup(ssn);
            Items.add(plugin);
        }
    }
    public void Uninstall(Session ssn, String Namespace){
        Plug p = getPlugin(Namespace);
        if (p!=null){
            Items.remove(p);
            p.Teardown(ssn);
        }
    }
    public Plug getPlugin(String Namespace){
        return Items.stream()
                .filter(p -> p.Header.getNamespace().equalsIgnoreCase(Namespace))
                .findFirst()
                .orElse(null);
    }
}
