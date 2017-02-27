package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

public interface Methods {
    PluginState Setup(Session ssn);
    PluginState Teardown(Session ssn);
    PluginState Execute(Session ssn, String Namespace, Item item);
}
