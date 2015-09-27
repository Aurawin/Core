package com.aurawin.core.lang;

import com.aurawin.core.storage.entities.UniqueId;

import java.util.Collection;

public class Namespace {

    public static class Entities{
        public static class Cloud{
            public static class Service{
                public static final UniqueId HTTP = new UniqueId("com.aurawin.core.storage.entities.cloud.service.http");
                public static final UniqueId SMTP = new UniqueId("com.aurawin.core.storage.entities.cloud.service.smtp");
                public static final UniqueId POP3 = new UniqueId("com.aurawin.core.storage.entities.cloud.service.pop3");
                public static final UniqueId IMAP = new UniqueId("com.aurawin.core.storage.entities.cloud.service.imap");
                public static final UniqueId XMPP = new UniqueId("com.aurawin.core.storage.entities.cloud.service.xmpp");
            }
        }
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
        List.add(Entities.Cloud.Service.HTTP);
        List.add(Entities.Cloud.Service.SMTP);
        List.add(Entities.Cloud.Service.POP3);
        List.add(Entities.Cloud.Service.IMAP);
        List.add(Entities.Cloud.Service.XMPP);

        List.add(Entities.Domain.UserAccount.Avatar);

        List.add(Entities.Domain.Folder.Domain);
        List.add(Entities.Domain.Folder.Social);

        List.add(Entities.Domain.Roster.Avatar);

        List.add(Entities.Domain.Network.ACL);
        List.add(Entities.Domain.Network.Avatar);

    }
}
