package com.aurawin.core.rsr.def.http;


public enum Status {
    sEmpty ( ""),
    // 1XX informational
    s100 ("100 Continue"),
    s101 ("101 Switching Protocols"),
    s102 ("102 Processing"),
    // 2XX Successful
    s200 ("200 OK"),
    s201 ("201 Created"),
    s202 ("202 Accepted"),
    s203 ("203 Non-Authoritative Information"),
    s204 ("204 No Content"),
    s205 ("205 Reset Content"),
    s206 ("206 Partial Content"),
    s207 ("207 Multi-Status"),
    s208 ("208 Already Reported"),
    s226 ("226 IM Used"),
    // 3XX Redirection
    s300 ("300 Multiple Choices"),
    s301 ("301 Moved Permanently"),
    s302 ("302 Found"),
    s303 ("303 See Other"),
    s304 ("304 Not Modified"),
    s305 ("305 Use Proxy"),
    s306 ("306 (Unused)"),
    s307 ("307 Temporary Redirect"),
    s308 ("308 Permanent Redirect"),
    // 4XX Client Error
    s400 ("400 Bad Request"),
    s401 ("401 Unauthorized"),
    s402 ("402 Payment Required"),
    s403 ("403 Forbidden"),
    s404 ("404 Not Found"),
    s405 ("405 Methods Not Allowed"),
    s406 ("406 Not Acceptable"),
    s407 ("407 Proxy Authentication Required"),
    s408 ("408 Request Timeout"),
    s409 ("409 Conflict"),
    s410 ("410 Gone"),
    s411 ("411 Length Required"),
    s412 ("412 Precondition Failed"),
    s413 ("413 Request Stored Too Large"),
    s414 ("414 Request-URI Too Long"),
    s415 ("415 Unsupported Media Type"),
    s416 ("416 Requested Range Not Satisfiable"),
    s417 ("417 Expectation Failed"),
    s418 ("418 I\'m a teapot"),
    s421 ("421 Misdirected Request"),
    s422 ("422 Unprocessable Entity"),
    s423 ("423 Locked"),
    s424 ("424 Failed Dependency"),
    s426 ("426 Upgrade Required"),
    s428 ("428 Precondition Required"),
    s429 ("429 Too Many Requests"),
    s431 ("431 Request Header Fields Too Large"),
    s451 ("451 Unavailable For Legal Reasons"),
    // 5XX Server Errror
    s500 ("500 Internal Server Error"),
    s501 ("501 Not Implemented"),
    s502 ("502 Bad Gateway"),
    s503 ("503 Service Unavailable"),
    s504 ("504 Gateway Timeout"),
    s505 ("505 HTTP Version Not Supported"),
    s506 ("506 Variant Also Negotiates"),
    s507 ("507 Insufficient Storage"),
    s508 ("508 Loop Detected"),
    s510 ("510 Not Extended"),
    s511 ("511 Network Authentication Required");


    Status(String value){
        this.value = value;
    }
    private final String value;

    public String getValue(){return value;}
    public static final Status Empty = sEmpty;
    public static Status fromString(String input){
        String i = input.substring(0,3);
        for ( Status s : Status.values()){
            if (s.value.startsWith(i)) return s;
        }
        return null;
    }
}

