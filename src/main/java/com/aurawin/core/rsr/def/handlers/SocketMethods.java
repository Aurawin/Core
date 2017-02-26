package com.aurawin.core.rsr.def.handlers;

public interface SocketMethods {
    SocketHandlerResult Send();
    SocketHandlerResult Recv();
    void Setup(boolean Accepted);
    void Teardown();
    void Shutdown();
}
