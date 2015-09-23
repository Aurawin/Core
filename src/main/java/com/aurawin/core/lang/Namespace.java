package com.aurawin.core.lang;

import com.aurawin.core.storage.entities.UniqueId;

import java.util.Collection;

public class Namespace {

    public static class Entities{
        public static class Domain {
            public static class UserAccount{
                public static final UniqueId Avatar = new UniqueId("com.aurawin.core.storage.entities.domain.useraccount.avatar");
            }

            public static class Folder {
                public static final UniqueId Domain = new UniqueId("com.aurawin.core.storage.entities.domain.folder.domain");
                public static final UniqueId Social = new UniqueId("com.aurawin.core.storage.entities.domain.folder.social");
            }
            public static class Roster{
                public static final UniqueId Avatar = new UniqueId("com.aurawin.core.storage.entities.domain.roster.avatar");
            }
            public static class Network {
                public static final UniqueId ACL = new UniqueId("com.aurawin.core.storage.entities.domain.network.acl");
                public static final UniqueId Avatar = new UniqueId("com.aurawin.core.storage.entities.domain.network.avatar");
            }
        }
    }
    public static void Register(Collection List ){
        List.add(Entities.Domain.UserAccount.Avatar);

        List.add(Entities.Domain.Folder.Domain);
        List.add(Entities.Domain.Folder.Social);

        List.add(Entities.Domain.Roster.Avatar);

        List.add(Entities.Domain.Network.ACL);
        List.add(Entities.Domain.Network.Avatar);

    }
}
