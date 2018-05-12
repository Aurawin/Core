package com.aurawin.core.keywords;

import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.annotations.Namespaced;
import com.aurawin.core.time.Time;

import java.time.Instant;

@Namespaced
public class Keyword {
    public Keywords Owner;
    public Instant Modified;
    public Instant LastBuild;
    public String Name;
    public Object Data;
    public KeywordMethod Builder;

    public Keyword(Keywords owner, String name, String value){
        Owner = owner;
        Name = name;
        Data = value;
        Modified = Time.instantUTC();
        LastBuild = Time.instantUTC().minusMillis(Settings.Keywords.InitialInstantReductionMillis);
    }
    public Keyword(Keywords owner, String name, Object data, KeywordMethod builder){
        Owner = owner;
        Name = name;
        Data = data;
        Builder = builder;
        Modified = Time.instantUTC();
        LastBuild = Time.instantUTC().minusMillis(Settings.Keywords.InitialInstantReductionMillis);
    }

    public void Release(){
        Owner = null;
        Modified = null;
        LastBuild = null;
        Name = null;
        Builder = null;
    }
}
