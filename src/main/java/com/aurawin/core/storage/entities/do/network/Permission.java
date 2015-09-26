package com.aurawin.core.storage.entities.domain.network;

public class Permission {
    public static final long None           = 0;
    public static final long List           = 1 << 0;
    public static final long Read           = 1 << 1;
    public static final long Write          = 1 << 2;
    public static final long Delete         = 1 << 3;
    public static final long Create         = 1 << 4;
    public static final long Permissions    = 1 << 5;


    public static final long All            = List | Read | Write | Delete | Create  | Permissions;
}
