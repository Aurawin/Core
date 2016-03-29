package com.aurawin.core.keywords;

import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;

import java.time.Instant;


public class Keyword {
    public Keywords Owner;
    public Instant Modified;
    public Instant LastBuild;
    public String Name;
    public KeywordMethod Builder;

    public Keyword(Keywords owner, String name){
        Owner = owner;
        Name = name;
        Modified = Time.instantUTC();
        LastBuild = Time.instantUTC().minusMillis(Settings.Keywords.InitialInstantReductionMillis);
    }
    public Keyword(Keywords owner, String name, KeywordMethod builder){
        Owner = owner;
        Name = name;
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
