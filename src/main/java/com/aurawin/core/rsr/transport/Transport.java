package com.aurawin.core.rsr.transport;

import com.aurawin.core.rsr.def.rsrResult;
import org.hibernate.Session;

public interface Transport {
    rsrResult onPeek();
    rsrResult onProcess(Session ssn);
    rsrResult onDisconnected();
    rsrResult onAccepted();
    rsrResult onRejected();
    rsrResult onError();
    rsrResult onFinalize();
    rsrResult onInitialize();
}
