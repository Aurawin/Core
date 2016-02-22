package com.aurawin.core.rsr;

import com.aurawin.core.rsr.def.rsrResult;

public interface IItem {
    rsrResult onPeek();
    rsrResult onProcess();
    rsrResult onDisconnected();
    rsrResult onAccepted();
    rsrResult onRejected();
    rsrResult onError();
    rsrResult onFinalize();
    rsrResult onInitialize();
}
