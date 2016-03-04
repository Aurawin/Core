package com.aurawin.core.array;


public class KeyItem {

    public String Name;
    public String Value;
    public long Id;
    public Object Data;
    public Boolean Streams;
    public Boolean Stale;
    public Boolean Restricted;

    public void Assign(KeyItem value){
        Name=value.Name;
        Value=value.Value;
        Id=value.Id;
        Data= value.Data;
        Streams = value.Streams;
        Stale = value.Stale;
        Restricted = value.Restricted;
    }

    public KeyItem() {
        this.Name="";
        this.Value="";
        this.Id=0;
        this.Data=null;
        this.Streams=true;
        this.Stale=false;
        this.Restricted =false;
    }
    public KeyItem(String name){
        this.Name=name;
        this.Value="";
        this.Id=0;
        this.Data=null;
        this.Streams=true;
        this.Stale=false;
        this.Restricted =false;
    }
    public KeyItem(String name, String value){
        this.Name=name;
        this.Value=value;
        this.Id=0;
        this.Data=null;
        this.Streams=true;
        this.Stale=false;
        this.Restricted =false;
    }
    public KeyItem(String name, Object value){
        this.Name=name;
        this.Value="";
        this.Data=value;
        this.Id=0;
        this.Data=null;
        this.Streams=true;
        this.Stale=false;
        this.Restricted =false;
    }
    public KeyItem(String name, long id){
        this.Name=name;
        this.Value="";
        this.Data=null;
        this.Id=id;
        this.Data=null;
        this.Streams=true;
        this.Stale=false;
        this.Restricted =false;
    }
    public KeyItem(KeyItem value){
        this.Assign(value);
    }
}