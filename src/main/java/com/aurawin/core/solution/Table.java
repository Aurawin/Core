package com.aurawin.core.solution;

public class Table {
    public static class DBMS{
        public static class Default{
            public static final String Encoding = "UTF-8";
        }
        public static final String Encoding=
                "BIG5,EUC_CN,EUC_JP,EUC_JIS_2004,EUC_KR,EUC_TW,GB18030,GBK,ISO_8859_5,ISO_8859_6,ISO_8859_7,ISO_8859_8,"+
                "JOHAB,KOI8R,KOI8U,LATIN1,LATIN2,LATIN3,LATIN4,LATIN5,LATIN6,LATIN7,LATIN8,LATIN9,LATIN10,MULE_INTERNAL,"+
                "SJIS,SHIFT_JIS_2004,SQL_ASCII,UHC,UTF-8,WIN866,WIN874,WIN1250,WIN1251,WIN1252,WIN1253,WIN1254,WIN1255,"+
                "WIN1256,WIN1257,WIN1258";
    }
    public static class RSR{
        public static class HTTP{
            public static class Method{
                public static final String Copy = "COPY";
                public static final String Delete = "DELETE";
                public static final String Get = "GET";
                public static final String Head = "Head";
                public static final String Lock = "Lock";
                public static final String MakeCollection  = "MKCOL";
                public static final String Move = "MOVE";
                public static final String Options = "OPTIONS";
                public static final String Post = "POST";
                public static final String PropertyFind = "PROPFIND";
                public static final String PropertyPatch = "PROPPATCH";
                public static final String Put = "PUT";
                public static final String Search = "SEARCH";
                public static final String Trace = "TRACE";
                public static final String Unlock = "UNLOCK";

            }
        }
        public static class IMAP{
            public static class Method{
                public static final String Append = "APPEND";
                public static final String Capabilities = "CAPA";
                public static final String Check = "CHECK";
                public static final String Close = "CLOSE";
                public static final String CopyUID="COPYUID";
                public static final String Created = "CREATED";
                public static final String Delete = "DELETE";
                public static final String Examine = "EXAMINE";
                public static final String Expunge = "EXPUNGE";
                public static final String Fetch = "FETCH";
                public static final String Id="ID";
                public static final String List="LIST";
                public static final String Login = "LOGIN";
                public static final String Logout ="LOGOUT";
                public static final String ListSubscribe = "LSUB";
                public static final String NoOp = "NOOP";
                public static final String Rename = "RENAME";
                public static final String Search = "SEARCH";
                public static final String Select = "SELECT";
                public static final String StartTLS = "STARTTLS";
                public static final String Status = "STATUS";
                public static final String Store = "STORE";
                public static final String Subscribe = "SUBSCRIBE";
                public static final String UId = "UID";
                public static final String UnSubscribe = "UNSUBSCRIBE";

            }
        }
    }
}
