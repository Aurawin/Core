package com.aurawin.core.lang;

public class Database {
    public static class Table{
        public static class Domain{
            public static final String UserAccounts = "tbl_d_uas";

        }
    }
    public static class Field{
        public static class Domain{
            public static class UserAccount{
                public static final String Id="itmid";
                public static final String User="itmun";
                public static final String Pass="itmpswd";
                public static final String Auth="itmauth";
                public static final String LastIP="itmlip";
                public static final String FirstIP="itmfip";
                public static final String LastLogin="itmlln";
                public static final String LockCount="itmlct";
            }

        }
    }
    public static class Config{
        public static class Automatic{
            public static final String Create = "create";
            public static final String Update = "update";
            public static final String CreateDrop = "create-drop";
            public static final String Validate = "validate";
        }
    }
    public static class Test{
        public static class Entities{
            public static class Domain{
                public static final String UserAccount = "/test/storage.entities.domain.UserAccount.json";
            }
        }
    }

}
