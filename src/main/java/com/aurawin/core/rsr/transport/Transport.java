package com.aurawin.core.rsr.transport;

import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import org.hibernate.Session;

import javax.net.ssl.SSLSocket;

public interface Transport {
    rsrResult onPeek();
    rsrResult onProcess(Session ssn);
    void Disconnected();
    void Connected();
    void Error();
    void Finalized();
    void Initialized();
    CredentialResult onCheckCredentials(Session ssn);
    void Teardown();
    void Setup();
    void Reset();
}
