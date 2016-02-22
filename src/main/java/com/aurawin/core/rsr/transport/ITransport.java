package com.aurawin.core.rsr.transport;

import com.aurawin.core.rsr.def.rsrResult;

public interface ITransport {
    rsrResult onPeek();
    rsrResult onProcess();
    rsrResult onDisconnected();
    rsrResult onAccepted();
    rsrResult onRejected();
    rsrResult onError();
    rsrResult onFinalize();
    rsrResult onInitialize();
}
