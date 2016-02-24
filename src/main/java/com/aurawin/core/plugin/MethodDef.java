package com.aurawin.core.plugin;

public class MethodDef {
    long Id;
    String Namespace;
    String Name;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public MethodDef(String namespace, String name) {
        Namespace = namespace;
        Name = name;
    }
}
