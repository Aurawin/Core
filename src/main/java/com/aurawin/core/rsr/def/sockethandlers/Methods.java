package com.aurawin.core.rsr.def.sockethandlers;


public interface Methods {
    HandlerResult Setup(boolean Accepted);
    HandlerResult Send();
    HandlerResult Teardown();
    HandlerResult Recv();
}
