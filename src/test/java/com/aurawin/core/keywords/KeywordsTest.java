package com.aurawin.core.keywords;

import com.aurawin.core.VarString;
import com.aurawin.core.keywords.Keyword;
import com.aurawin.core.keywords.Keywords;
import org.junit.Test;

import java.util.ArrayList;


public class KeywordsTest {
    public static final String defaultResource = "/keywords.text";
    private Keywords keywords;
    private String Output;
    private String Input;
    @Test
    public void testList(){
        Keywords.Default.Master.Find("date");
        keywords= new Keywords();
        try {
            Input = VarString.fromResource(defaultResource);
            ArrayList<Keyword> items  = keywords.fromString(Input);
            keywords.addAll(items);
            for (Keyword k:items){

            }
            Output = keywords.Stream();

        } finally{
            Output="";
            keywords.Release();
            keywords=null;
        }

    }

}