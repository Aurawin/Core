package com.aurawin.core.lang;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class Database {
    public static class Table{
        public static class Stored{
            public static final String Module = "tbl_s_mle";
            public static final String Certificate = "tbl_s_cert";
        }
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
                public static final String value = "from Module where Namespace=:Namespace";
            }
        }
        public static class Plugin{
            public static class ById {
                public static final String name = "QueryPluginById";
                public static final String value = "from Plugin where Id=:Id";
            }
            public static class ByNamespace {
                public static final String name = "QueryPluginByNamspace";
                public static final String value = "from Plugin where Namespace=:Namespace";
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
            public static final String TextKey = "ittk";
            public static final String DerKey = "itdk";
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
