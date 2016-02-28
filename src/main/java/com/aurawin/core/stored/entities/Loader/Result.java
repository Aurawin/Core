package com.aurawin.core.stored.entities.loader;

import com.aurawin.core.stored.entities.Module;

public class Result {
    public enum Kind { Exception, NotFound, Found }
    public Kind State;
    public Module Module;
    public Class Class;

    public Result(){
        State = Kind.NotFound;
        Module=null;
        Class=null;

    }
    public Result(Module module) {
        State=(module!=null) ? Kind.Found : Kind.NotFound;
        Module = module;
        Class=null;
    }
}
