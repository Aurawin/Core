package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.stored.entities.security.Credentials;

public interface DecryptCredentials {
    rsrResult decryptCredentials(Item RSR, String... params);
}
