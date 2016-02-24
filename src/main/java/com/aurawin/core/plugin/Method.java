package com.aurawin.core.plugin;

public interface Method {
    MethodState BeforeExecute();
    MethodState AfterExecute();
}
