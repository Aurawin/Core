package com.aurawin.core.rsr.transport.methods;


public enum Result {
    Ok ("Ok"),
    Failure("Method Failure"),
    NotFound("Method Not Found"),
    AlreadyRegistered("Method Already Registered"),
    Exception("Method Exception");

    Result(String value){
        this.value = value;
    }
    private final String value;

    public String getValue(){return value;}
}
