package com.aurawin.core.rsr.def.handlers;

import java.io.IOException;

public interface SocketMethods {
    SocketHandlerResult Send();
    SocketHandlerResult Recv();
    void Setup();
    void Shutdown();
    void Teardown();
    void beginHandshake();
}
