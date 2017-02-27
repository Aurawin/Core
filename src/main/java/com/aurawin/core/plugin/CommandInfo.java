package com.aurawin.core.plugin;



import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.plugin.annotations.Command;
import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

public class CommandInfo extends com.aurawin.core.stored.entities.UniqueId {
    public Plugin annotationPlugin = null;
    public Command annotationCommand = null;
    protected java.lang.reflect.Method Method;
    protected Plug Plugin;

    public PluginState Execute(Session ssn, Item Transport) throws IllegalAccessException,InvocationTargetException{
        if (Method!=null) {
            return (PluginState) Method.invoke(Plugin,ssn,Transport);
        } else {
            return PluginState.PluginMethodNotFound;
        }
    }


}
