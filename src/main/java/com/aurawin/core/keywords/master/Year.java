package com.aurawin.core.keywords.master;


import com.aurawin.core.keywords.Keyword;
import com.aurawin.core.keywords.KeywordMethod;
import com.aurawin.core.keywords.Keywords;

public class Year extends Keyword implements KeywordMethod {
    public String Evaluate(){
        return com.aurawin.core.time.Time.yearOnly(new java.util.Date());
    }
    public Year(Keywords owner, String name,String value) {
        super(owner, name, value);
        Builder = this;
    }
}
