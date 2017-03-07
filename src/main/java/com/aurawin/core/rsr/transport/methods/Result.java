package com.aurawin.core.rsr.transport.methods;


public enum Result {
    None("None"),
    Ok ("Ok"),
    Failure("Methods Failure"),
    NotFound("Methods Not Found"),
    NotAuthorizied("Methods Not Authorized"),
    AlreadyRegistered("Methods Already Registered"),
    Exception("Methods Exception");

    Result(String value){
        this.value = value;
    }
    private final String value;

    public String getValue(){return value;}
}
