package com.aurawin.core.lang;

import com.aurawin.core.storage.entities.UniqueId;

public class Namespace {

    public static class Entities{
        public static class Folder {
            public static final UniqueId Domain = new UniqueId("com.aurawin.core.storage.entities.folder.domain");
            public static final UniqueId Social = new UniqueId("com.aurawin.core.storage.entities.folder.social");
        }
        public static class Network {
            public static final UniqueId ACL = new UniqueId("com.aurawin.core.storage.entities.network.acl");
        }
    }
}
