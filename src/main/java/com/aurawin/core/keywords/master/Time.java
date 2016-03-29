package com.aurawin.core.keywords.master;

import com.aurawin.core.keywords.Keyword;
import com.aurawin.core.keywords.KeywordMethod;
import com.aurawin.core.keywords.Keywords;

public class Time extends Keyword implements KeywordMethod {
    public String Evaluate(){
        return com.aurawin.core.time.Time.timeOnly(new java.util.Date());
    }
    public Time(Keywords owner, String name) {
        super(owner, name);
        Builder = this;
    }
}
