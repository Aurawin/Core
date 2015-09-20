package com.aurawin.core.lang;

import com.aurawin.core.array.KeyItem;
import org.hibernate.Query;
import org.hibernate.Session;

public class Database {
    public static class Table{
        public static class Domain{
            public static final String UserAccounts = "tbl_d_uas";

        }
    }
    public static class Query{
        public static class Domain{
            public static class UserAccount{
                public static class lookupByAuth {
                    public static final String name = "lookupByAuth";
                    public static final String value = "from UserAccount where Auth = :Auth";
                    public static org.hibernate.Query Create(Session ssn, String Auth){
                        return ssn.getNamedQuery(name)
                                .setString("Auth",Auth);
                    }
                }
                public static class lookupById {
                    public static final String name = "lookupById";
                    public static final String value = "from UserAccount where Id = :Id";
                    public static org.hibernate.Query Create(Session ssn, long Id){
                        return ssn.getNamedQuery(name)
                                .setLong("Id",Id);
                    }
                }
            }

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
                public static final String LastConsumptionCalc="itmlcc";
                public static final String Consumption="itmcspn";
                public static final String Quota="itmquo";
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
