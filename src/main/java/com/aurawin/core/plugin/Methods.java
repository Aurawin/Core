package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.stored.Stored;
import org.hibernate.Session;

import java.util.Collection;

public interface Methods {
    PluginState Setup(Session ssn);
    PluginState Teardown(Session ssn);
    PluginState Execute(Session ssn, String Namespace, Item item);
}
