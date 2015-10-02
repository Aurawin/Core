package com.aurawin.core.array;


public class KeyItem {

    public String Name;
    public String Value;
    public long Id;
    public Object Data;

    public void Assign(KeyItem value){
        Name=value.Name;
        Value=value.Value;
        Id=value.Id;
        Data= value.Data;
    }

    public KeyItem() {
        this.Name="";
        this.Value="";
        this.Id=0;
        this.Data=null;
    }
    public KeyItem(String name){
        this.Name=name;
        this.Value="";
        this.Id=0;
        this.Data=null;
    }
    public KeyItem(String name, String value){
        this.Name=name;
        this.Value=value;
        this.Id=0;
        this.Data=null;
    }
    public KeyItem(KeyItem value){
        this.Assign(value);
    }
}