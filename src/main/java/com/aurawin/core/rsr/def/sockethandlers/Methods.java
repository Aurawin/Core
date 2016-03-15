package com.aurawin.core.rsr.def.sockethandlers;


import java.net.Socket;

public interface Methods {
    HandlerResult Send();
    HandlerResult Recv();
    void Setup(boolean Accepted);
    void Teardown();
    void Shutdown();
}
