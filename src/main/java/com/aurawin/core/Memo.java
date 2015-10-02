package com.aurawin.core;

public class Memo {
    protected String Data;
    public Memo() {
        Data="";
    }
    public Memo(String data){
        Data=data;
    }
    public Memo(Memo src){
        Assign(src);
    }
    public void Assign(Memo src){
        Data=src.Data;
    }
    public void Empty(){
        Data="";
    }
    public int compareTo(Memo src){
        return Data.compareTo(src.Data);
    }
    public boolean equalTo(Object src){
        return ( (src instanceof Memo) && (Data.compareTo( ((Memo)src).Data))==0);
    }
}
