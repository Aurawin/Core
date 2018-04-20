package com.aurawin.core.plugin;

import com.aurawin.core.json.Builder;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.UniqueId;
import com.aurawin.core.stream.MemoryStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.plugin.annotations.Command;

import static com.aurawin.core.plugin.PluginState.PluginSuccess;

public abstract class Plug extends UniqueId implements Methods {
    public Plugin Annotation;
    private ConcurrentHashMap<String,CommandInfo>Commands;
    private Builder bldr;
    public Gson gson;

    @SuppressWarnings("unchecked")
    public ArrayList<CommandInfo>discoverCommands(){
        return new ArrayList<CommandInfo>(Commands.values());
    }

    public void writeObject(MemoryStream Stream, Object Data){
        Stream.Write(gson.toJson(Data));
    }
    public void writeObjects(MemoryStream Stream, Object[] Data){
        Stream.Write(gson.toJson(Data));
    }
    public void writeObjects(MemoryStream Stream, ArrayList<? extends Object> Data){
        Stream.Write(gson.toJson(Data));
    }
    public <T extends Stored>T readObject(MemoryStream Stream,Class<T> clazz){
        return gson.fromJson(Stream.toString(),clazz);
    }
    public <T extends Stored>T[] readObjects(MemoryStream Stream, Class<T> clazz){
        return gson.fromJson(Stream.toString(),new TypeToken<T>(){}.getType());
    }
    public CommandInfo getCommand(String Namespace, String Method){
        String key = Namespace + "."+Method;
        return Commands.get(key);
    }
    public Collection<CommandInfo> getCommands(){
       return Commands.values();

    }
    public PluginState Setup(Session ssn){
        bldr = new Builder();
        gson = bldr.Create();
        PluginState r = PluginState.PluginFailure;
        Annotation=getClass().getAnnotation(Plugin.class);
        if (Annotation!=null) {
            setNamespace(
                    com.aurawin.core.solution.Namespace.Entities.Plugin.getNamespace(
                            Annotation.Package(),
                            Annotation.Name()
                    )
            );
            Identify(ssn);
            ConcurrentHashMap<String, CommandInfo> ms = new ConcurrentHashMap<>();
            java.lang.reflect.Method[] fs = getClass().getMethods();
            Command aC = null;
            for (java.lang.reflect.Method f : fs) {
                aC = f.getAnnotation(Command.class);
                if (aC != null) {
                    String ns= com.aurawin.core.solution.Namespace.Entities.Plugin.buildEntryNamespace(
                            Annotation.Package(),
                            Annotation.Name(),
                            aC.Name(),
                            aC.Method()
                    );
                    CommandInfo ci = new CommandInfo();
                    ci.setNamespace(ns);
                    ci.Identify(ssn);
                    ci.Entry=f;
                    ci.annotationPlugin=Annotation;
                    ci.annotationCommand=aC;
                    ci.Plugin=this;
                    String key = aC.Namespace()+"."+ aC.Method();
                    ms.put(key,ci);
                }
            }
            Commands = ms;
            Plugins.Register(this);
            r =  PluginSuccess;
        } else {
            r = PluginState.PluginAnnotationError;
        }
        return r;
    }
    public PluginState Teardown(Session ssn){
        gson = null;
        bldr = null;
        return PluginSuccess;
    }
    @Override
    public PluginState Execute(Session ssn, String Namespace, Item itm) {
        CommandInfo ci = Commands.get(Namespace);
        if (ci!=null) {
            try {
                return (PluginState) ci.Entry.invoke(this,ssn,itm);
            } catch (Exception e) {
                return PluginState.PluginException;
            }
        }
        return PluginState.PluginMethodNotFound;
    }

    public void discoverRole(String role, ArrayList<UniqueId> manifest){
        if (Arrays.asList(Annotation.Roles()).contains(role) ){
            manifest.add(this);
            for (CommandInfo ci : Commands.values()){
                if (Arrays.asList(ci.annotationCommand.Roles()).contains(role)){
                    manifest.add(ci);
                }
            }
        }
    }
}
