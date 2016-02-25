package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

public interface Method {
    MethodState Setup(Session ssn);
    MethodState Teardown(Session ssn);
    MethodState Execute(Session ssn, String Namespace, Item item);
}
