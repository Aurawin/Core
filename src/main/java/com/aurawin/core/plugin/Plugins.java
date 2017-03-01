package com.aurawin.core.plugin;

import com.aurawin.core.lang.Namespace;
import org.hibernate.Session;
import com.aurawin.core.plugin.annotations.Plugin;
import java.util.ArrayList;
import java.util.HashMap;

public class Plugins {
    volatile HashMap<String,Plug> Items;
    public Plugins() {
        Items = new HashMap<String,Plug>();
    }
    public void Install(Session ssn, Plug plugin) {
        if (plugin.Annotation==null) {
            plugin.Annotation = plugin.getClass().getAnnotation(Plugin.class);
        }
        if (plugin.Header.getId() == 0){
            plugin.Header.setNamespace(
                    Namespace.Entities.Plugin.getNamespace(
                            plugin.Annotation.Package(),
                            plugin.Annotation.Name()
                    )
            );
        }
        Plug p = getPlugin(plugin.Annotation.Namespace());
        if (p==null) {
            plugin.Setup(ssn);
            Items.put(plugin.Annotation.Namespace(),plugin);
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
        return Items.get(Namespace);
    }
}
