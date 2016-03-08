package com.aurawin.core.rsr.def.sockethandlers;


public interface Methods {
    HandlerResult Setup();
    HandlerResult Teardown();
    HandlerResult Send();
    HandlerResult Recv();
}
