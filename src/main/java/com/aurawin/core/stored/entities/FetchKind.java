package com.aurawin.core.stored.entities;


public enum FetchKind {
    None (0),
    Primary (1),
    Custom(2),
    Infinite (-1);

    FetchKind(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){return value;}
    public void setCustomValue(int value){
        Custom.value=value;
    }
}

