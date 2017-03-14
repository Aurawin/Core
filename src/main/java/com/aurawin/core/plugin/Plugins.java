package com.aurawin.core.plugin;

import com.aurawin.core.lang.Namespace;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.UniqueId;
import org.hibernate.Session;
import com.aurawin.core.plugin.annotations.Plugin;
import org.hibernate.collection.internal.PersistentBag;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Plugins {
    private static ConcurrentHashMap<String,Plug>Master = new ConcurrentHashMap<>();

    public void Install(Session ssn, Plug plugin) {
        if (plugin.Annotation==null) {
            plugin.Annotation = plugin.getClass().getAnnotation(Plugin.class);
        }
        if (plugin.getId() == 0){
            plugin.setNamespace(
                    Namespace.Entities.Plugin.getNamespace(
                            plugin.Annotation.Package(),
                            plugin.Annotation.Name()
                    )
            );
        }
        Plug p = getPlugin(plugin.Annotation.Namespace());
        if (p==null) {
            plugin.Setup(ssn);
            Master.put(plugin.Annotation.Namespace(),plugin);
        }
    }
    public void Uninstall(Session ssn, String Namespace){
        Plug p = Master.remove(Namespace);
        if (p!=null){

            p.Teardown(ssn);
        }
    }
    public Plug getPlugin(String Namespace){
        return Master.get(Namespace);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Plug> toArrayList(){
        return new ArrayList<>(Master.values());
    }
    public static void Register(Plug plug) {
        if (Master.get(plug.Namespace)==null) {
            Master.put(plug.Namespace,plug);
        }

    }
    public static ArrayList<UniqueId> listAll(){
        ArrayList<UniqueId> al = new ArrayList<>();
        for (Plug p : Master.values()) {
            al.add(p);
            for (CommandInfo ci : p.Commands.values()){
                al.add(ci);
            }
        }
        return al;
    }
    public static void discoverRole(String Role, ArrayList<UniqueId>Manifest) {
        for (Plug p : Master.values()) {
            p.discoverRole(Role,Manifest);
        }
    }
}
