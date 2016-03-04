package com.aurawin.core.rsr.def;

import java.util.EnumSet;

public enum CredentialResult {
    None,Passed,Failed,Blocked,UnknownMethod;
    public static final EnumSet<CredentialResult> Stop = EnumSet.of(Failed,UnknownMethod,Blocked);
}

