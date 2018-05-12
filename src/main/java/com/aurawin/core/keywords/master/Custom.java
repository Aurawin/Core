package com.aurawin.core.keywords.master;

import com.aurawin.core.keywords.Keyword;
import com.aurawin.core.keywords.KeywordMethod;
import com.aurawin.core.keywords.Keywords;

public class Custom extends Keyword implements KeywordMethod{
    public String Evaluate(){
        return (String) Data;
    }
    public Custom(Keywords owner, String name, String value) {
        super(owner, name, value);
        Builder = this;
    }
}
