package com.aurawin.core.rsr.def.http;

public enum Status {
    // 1XX informational
    s100 ("100 Continue"),
    s101 ("101 Switching Protocols"),
    // 2XX Successful
    s200 ("200 OK"),
    s201 ("201 Created"),
    s202 ("202 Accepted"),
    s203 ("203 Non-Authoritative Information"),
    s204 ("204 No Content"),
    s205 ("205 Reset Content"),
    s206 ("206 Partial Content"),
    // 3XX Redirection
    s300 ("300 Multiple Choices"),
    s301 ("301 Moved Permanently"),
    s302 ("302 Found"),
    s303 ("303 See Other"),
    s304 ("304 Not Modified"),
    s305 ("305 Use Proxy"),
    s306 ("306 (Unused)"),
    s307 ("307 Temporary Redirect"),
    // 4XX Client Error
    s400 ("400 Bad Request"),
    s401 ("401 Unauthorized"),
    s402 ("402 Payment Required"),
    s403 ("403 Forbidden"),
    s404 ("404 Not Found"),
    s405 ("405 Method Not Allowed"),
    s406 ("406 Not Acceptable"),
    s407 ("407 Proxy Authentication Required"),
    s408 ("408 Request Timeout"),
    s409 ("409 Conflict"),
    s410 ("410 Gone"),
    s411 ("411 Length Required"),
    s412 ("412 Precondition Failed"),
    s413 ("413 Request Entity Too Large"),
    s414 ("414 Request-URI Too Long"),
    s415 ("415 Unsupported Media Type"),
    s416 ("416 Requested Range Not Satisfiable"),
    s417 ("417 Expectation Failed"),
    // 5XX Server Errror
    s500 ("500 Internal Server Error"),
    s501 ("501 Not Implemented"),
    s502 ("502 Bad Gateway"),
    s503 ("503 Service Unavailable"),
    s504 ("504 Gateway Timeout"),
    s505 ("505 HTTP Version Not Supported");

    private Status(String value){
        this.value = value;
    }
    private final String value;

    public String getValue(){return value;}
}

