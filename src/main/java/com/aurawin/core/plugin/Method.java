package com.aurawin.core.plugin;

import org.hibernate.Session;

public interface Method {
    MethodState BeforeExecute();
    MethodState AfterExecute();
    MethodState Setup(Session ssn);
    MethodState Teardown(Session ssn);
}
