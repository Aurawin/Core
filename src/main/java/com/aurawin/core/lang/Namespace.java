package com.aurawin.core.lang;

import com.aurawin.core.stored.entities.UniqueId;

import java.util.ArrayList;
import java.util.Collection;

public class Namespace {
    public static class Entities{
        public static class Plugin{
            public static String getNamespace(String Package,String Name){
                return "$package.$name"
                        .replace("$package",Package)
                        .replace("$name",Name);
            }
            public static String getMethodNamespace(String Package,String Name,String Method){
                return "$package.$name.$method"
                        .replace("$package",Package)
                        .replace("$name",Name)
                        .replace("$method",Method);
            }
        }
        public static class Cloud{
            public static class Service{
                public static final UniqueId HTTP = new UniqueId("com.aurawin.core.stored.entities.cloud.service.http");
                public static final UniqueId SMTP = new UniqueId("com.aurawin.core.stored.entities.cloud.service.smtp");
                public static final UniqueId POP3 = new UniqueId("com.aurawin.core.stored.entities.cloud.service.pop3");
                public static final UniqueId IMAP = new UniqueId("com.aurawin.core.stored.entities.cloud.service.imap");
                public static final UniqueId XMPP = new UniqueId("com.aurawin.core.stored.entities.cloud.service.xmpp");
            }
        }

    }

    public static ArrayList<UniqueId> Discover(){
        ArrayList<UniqueId> l = new ArrayList<UniqueId>();
        l.add(Entities.Cloud.Service.HTTP);
        l.add(Entities.Cloud.Service.SMTP);
        l.add(Entities.Cloud.Service.POP3);
        l.add(Entities.Cloud.Service.IMAP);
        l.add(Entities.Cloud.Service.XMPP);
        return l;
    }
}
