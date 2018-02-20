package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.rsrResult;

public abstract class Mechanism implements Authenticate,Login,
        DecryptCredentials,ChallengeBuilder, AuthorizationBuilder{
    public String Key;

    public Mechanism(String key) {
        Key = key;
    }
}
