package com.aurawin.core.keywords.master;

import com.aurawin.core.keywords.Keyword;
import com.aurawin.core.keywords.KeywordMethod;
import com.aurawin.core.keywords.Keywords;
import com.aurawin.core.time.Time;

public class Date extends Keyword implements KeywordMethod{
    public String Evaluate(){
        return Time.dateOnly(new java.util.Date());
    }
    public Date(Keywords owner, String name, String value) {
        super(owner, name, value);
        Builder = this;
    }
}
