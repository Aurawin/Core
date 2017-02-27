package com.aurawin.core.rsr.def.http;

public class Field {

    public static final String Accept = "Accept";
    public static final String AcceptCharset = "Accept-Charset";
    public static final String AcceptEncoding = "Accept-Encoding";
    public static final String AcceptLanguage= "Accept-Language";
    public static final String AcceptRanges = "Accept-Ranges";
    public static final String AccessControl = "Access-Control";
    public static final String AccessControlAllowOrigin = "Access-Control-Allow-Origin";
    public static final String AccessControlAllowHeaders = "Access-Control-Allow-Headers";
    public static final String AccessControlAllowMethods = "Access-Control-Allow-SocketMethods";
    public static final String AccessControlAllowMethod = "Access-Control-Allow-Methods";
    public static final String AccessControlExposeHeaders = "Access-Control-Expose-Headers";
    public static final String AccessControlMaxAge= "Access-Control-Max-Age";
    public static final String AccessControlRequestHeaders= "Access-Control-Request-Headers";
    public static final String AccessControlRequestMethod="Access-Control-Request-Methods";
    public static final String AccessControlAllowCredentials="Access-Control-Allow-Manifest";

    public static final String Allow = "Allow";
    public static final String Authorization = "Authorization";

    public static final String Cookie= "Cookie";
    public static final String Date = "Date";
    public static final String DAV = "DAV";
    public static final String Expires = "Expires";
    public static final String From = "From";
    public static final String Vary = "Vary";
    public static final String Host = "Host";
    public static final String IfMatch = "If-Match";
    public static final String IfNoneMatch = "If-None-Match";
    public static final String Upgrade = "Upgrade";
    public static final String Origin = "Origin";
    public static final String Connection = "Connection";
    public static final String ContentType = "Content-Type";
    public static final String ContentDisposition = "Content-Disposition";
    public static final String ContentEncoding = "Content-Encoding";
    public static final String ContentTransferEncoding = "Content-Transfer-Encoding";
    public static final String ContentLanguage = "Content-Language";
    public static final String ContentLength = "Content-Length";
    public static final String ContentRange = "Content-Range";
    public static final String CacheControl = "Cache-Control";

    public static final String Deflate = "Deflate";

    public static final String IfModifiedSince = "If-Modified-Since";
    public static final String IfRange = "If-Range";
    public static final String RemoteIP = "Remote-IP";
    public static final String LastModified = "Last-Modified";
    public static final String Location = "Location";
    public static final String Pragma = "Pragma";
    public static final String Range = "Range";
    public static final String Referer = "Referer";
    public static final String RetryAfter = "Retry-After";
    public static final String Server = "Server";
    public static final String SetCookie = "Set-Cookie";
    public static final String UserAgent = "User-Agent";
    public static final String WWWAuthenticate = "WWW-Authenticate";

    public static final String SecWebSocketAccept= "Sec-WebSocket-Accept";
    public static final String SecWebSocketKey = "Sec-WebSocket-Key";
    public static final String SecWebSocketKey1 = "Sec-WebSocket-Key1";
    public static final String SecWebSocketKey2 = "Sec-WebSocket-Key2";
    public static final String SecWebSocketProtocol = "Sec-WebSocket-Protocol";
    public static final String SecWebSocketProtocolClient = "Sec-WebSocket-Protocol-Client";
    public static final String SecWebSocketProtocolServer = "Sec-WebSocket-Protocol-Server";

    public static final String SecWebSocketOrigin = "Sec-WebSocket-Origin";
    public static final String SecWebSocketLocation = "Sec-WebSocket-Location";
    public static final String SecWebSocketExtensions = "Sec-WebSocket-Extensions";

    public static final String SecWebSocketVersion = "Sec-WebSocket-Version";
    public static final String SecWebSocketVersionClient = "Sec-WebSocket-Version-Client";
    public static final String SecWebSocketVersionServer = "Sec-WebSocket-Version-Server";

    public static final String TransferEncoding = "Transfer-Encoding";

    public static final String Parameters = "Parameters";
    public static final String ETag = "ETag";
    public static final String Id = "Id";
    public static final String ResourceId = "Rcid";
    public static final String UserKind = "Euk";
    public static final String Auth = "Auth";
    public static final String User = "User";
    public static final String Code = "Code";
    public static final String Search = "Srch";
    public static final String Depth = "Depth";
    public static final String NameSpace = "NS";
    public static final String Kind = "KIND";
    public static final String CoreObjectNamespace = "Co-Ns";
    public static final String CoreCommandNamespace = "Cc-Ns";

    public static final String GuidWebSocket = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public static class Value{
        public static class Authenticate{
            public static class Basic{
                public static final String Message(String Realm){
                    return "Basic realm=\""+Realm+"\"";
                }
            }
        }
    }
}
