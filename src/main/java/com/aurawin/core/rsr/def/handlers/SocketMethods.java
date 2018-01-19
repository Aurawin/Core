package com.aurawin.core.rsr.def.handlers;

import java.io.IOException;

public interface SocketMethods {
    SocketHandlerResult Send();
    SocketHandlerResult Recv();
    void Setup();
    void Teardown();
    void Shutdown();
    void beginHandshake() throws IOException;
}
