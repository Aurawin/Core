package com.aurawin.core.lang;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class Database {
    public static class Table{
        public static final String Module = "tbl_s_mle";
        public static final String Certificate = "tbl_s_crt";
        public static final String LoginFailure = "tbl_s_lif";
        public static final String Ban = "tbl_s_ban";
        public static final String Plugin = "tbl_k_pgn";
        public static final String UniqueId = "tbl_k_uid";
    }
    public static class Query{
        public static class Module{
            public static class ById {
                public static final String name = "QueryModuleById";
                public static final String value = "from Module where Id=:Id";
            }
            public static class ByNamespace {
                public static final String name = "QueryModule";
                public static final String value = "from Module where Namespaced=:Namespaced";
            }
        }
        public static class Plugin{
            public static class ById {
                public static final String name = "QueryPluginById";
                public static final String value = "from Plugin where Id=:Id";
            }
            public static class ByNamespace {
                public static final String name = "QueryPluginByNamspace";
                public static final String value = "from Plugin where Namespaced=:Namespaced";
            }
        }
        public static class UniqueId{
            public static class ById {
                public static final String name = "QueryUniqueIdById";
                public static final String value = "from UniqueId where Id=:Id";
            }
            public static class ByNamespace {
                public static final String name = "QueryUniqueIdByNamspace";
                public static final String value = "from UniqueId where Namespace=:Namespace";
            }
        }
        public static class Certificate{
            public static class ById {
                public static final String name = "QueryCertificateById";
                public static final String value = "from Certificate where Id=:Id";
            }
            public static class All {
                public static final String name = "QueryCertificateAll";
                public static final String value = "from Certificate";
            }
            public static class ByDomainId {
                public static final String name = "QueryCertificateByDomainId";
                public static final String value = "from Certificate where DomainId=:DomainId";
            }
        }
        public static class LoginFailure{
            public static class ById {
                public static final String name = "QueryLoginFailureById";
                public static final String value = "from LoginFailure where Id=:Id";
            }
            public static class ByIp {
                public static final String name = "QueryLoginFailureByIp";
                public static final String value = "from LoginFailure where Ip=:Ip order by Instant";
            }
            public static class BetweenInstant {
                public static final String name = "QueryLoginFailureByInstant";
                public static final String value = "from LoginFailure where Instant>:InstantLow and Instant<sInstantHigh";
            }
        }

        public static class Ban{
            public static class ById {
                public static final String name = "QueryBanById";
                public static final String value = "from Ban where Id=:Id";
            }
            public static class ByIp {
                public static final String name = "QueryBanByIp";
                public static final String value = "from Ban where Ip=:Ip";
            }

        }
    }
    public static class Field{
        public static class Module{
            public static final String Id="itmid";
            public static final String Locked="itmlkd";
            public static final String Name="itnme";
            public static final String Package="itpkg";
            public static final String Namespace="itmns";
            public static final String Source = "itmsrc";
            public static final String Revision = "itmrv";
            public static final String Build = "itmbld";
            public static final String Code = "itmcde";
        }
        public static class Plugin{
            public static final String Id="itmid";
            public static final String Namespace="itmns";
        }
        public static class UniqueId{
            public static final String Id="itmid";
            public static final String Namespace="itmns";
        }
        public static class Certificate{
            public static final String Id="itmid";
            public static final String DomainId="itmdid";
            public static final String ChainCount = "itcc";
            public static final String Expires = "itexp";
            public static final String Created = "itctd";
            public static final String TextRequest = "irq";
            public static final String DerRequest = "irder";
            public static final String KeyPrivate = "idkp";
            public static final String KeyPublic = "ikpb";
            public static final String Request = "itrq";
            public static final String TextCert1 = "imtc1";
            public static final String TextCert2 = "imtc2";
            public static final String TextCert3 = "imtc3";
            public static final String TextCert4 = "imtc4";
            public static final String DerCert1 = "itmdc1";
            public static final String DerCert2 = "itmdc2";
            public static final String DerCert3 = "itmdc3";
            public static final String DerCert4 = "itmdc4";
        }
        public static class LoginFailure{
            public static final String Id="itmid";
            public static final String DomainId="itmdid";
            public static final String UserId="itmuid";
            public static final String Ip="itmip";
            public static final String Instant="itmisn";
            public static final String Username="itmun";
            public static final String Password="itmpd";
            public static final String Digest="itmdsgt";
        }
        public static class Ban{
            public static final String Id="itmid";
            public static final String Ip="itmip";
        }
    }
    public static class Config{
        public static class Automatic{
            public static class Commit {
                public static final Boolean On=true;
                public static final Boolean Off=false;
            }
            public static final String Create = "create";
            public static final String Update = "update";
            public static final String CreateDrop = "create-drop";
            public static final String Validate = "validate";
        }
    }
    public static class Test{
        public static class Entities{

        }
    }

}
