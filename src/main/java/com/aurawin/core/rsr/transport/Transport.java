package com.aurawin.core.rsr.transport;

import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import org.hibernate.Session;

public interface Transport {
    rsrResult onPeek();
    rsrResult onProcess(Session ssn);
    rsrResult onDisconnected();
    rsrResult onConnected();
    rsrResult onError();
    rsrResult onFinalize();
    rsrResult onInitialize();
    CredentialResult onCheckCredentials(Session ssn);
    void Teardown();
    void Setup();
}
