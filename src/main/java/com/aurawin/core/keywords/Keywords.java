package com.aurawin.core.keywords;

import com.aurawin.core.CriticalBlock;
import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.solution.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Keywords extends ConcurrentLinkedQueue<Keyword> {
    public static class System {
        public static final KeyPair Master = new KeyPair();
    }
    private CriticalBlock Lock;

    public ArrayList<Keyword>fromString(String Input) {
        // Keywords become manifest collection to stream output as String
        ArrayList<Keyword> Replacement = new ArrayList<Keyword>();
        String name = "";
        int idxInput = 0;
        int idxPhrase = -1;
        int idxSubPhrase =-1;
        int idxPhraseEnd = -1;
        int idxSubPhraseEnd = -1;
        int idxBefore = 0;
        int lengthInput = Input.length();
        while (idxInput<lengthInput){
            idxPhrase = Input.indexOf(Settings.Keywords.Phrase.Start,idxInput);
            if (idxPhrase!=-1) {
                idxPhraseEnd = Input.indexOf(Settings.Keywords.Phrase.End,idxPhrase+Settings.Keywords.Phrase.StartLength);
                if (idxPhraseEnd>-1) {
                    // we found a new keyword.
                    if (idxBefore<idxPhrase){
                        // we need to prepend a keyword to store data prior
                        String preKeyword = Input.substring(idxBefore,idxPhrase-1);
                        KeywordMethod km = new KeywordMethod() {
                            @Override
                            public String Evaluate() {
                                return preKeyword;
                            }
                        };
                        Keyword kw = new Keyword(this, "", km);
                        Replacement.add(kw);
                    }
                    idxInput = idxPhraseEnd+Settings.Keywords.Phrase.EndLength;
                    idxBefore=idxInput;

                    name = Input.substring(idxPhrase,idxPhraseEnd-1).trim();
                    KeyItem ki = System.Master.Find(name);
                    if (ki!=null) {
                        idxSubPhrase=ki.Value.indexOf(Settings.Keywords.Phrase.Start);
                        idxSubPhraseEnd = ki.Value.indexOf(Settings.Keywords.Phrase.End,idxSubPhrase);

                        if ((idxSubPhrase>-1) && (idxSubPhraseEnd>-1)) {
                            ArrayList<Keyword> subKeys = fromString(ki.Value);
                            Replacement.addAll(subKeys);
                        } else {
                            KeywordMethod km = new KeywordMethod() {
                                @Override
                                public String Evaluate() {
                                    return ki.Value;
                                }
                            };
                            Keyword kw = new Keyword(this, name, km);
                            Replacement.add(kw);
                        }
                    }

                } else {
                    String preKeyword = Input.substring(idxBefore);
                    KeywordMethod km = new KeywordMethod() {
                        @Override
                        public String Evaluate() {
                            return preKeyword;
                        }
                    };
                    Keyword kw = new Keyword(this, "", km);
                    Replacement.add(kw);
                    idxInput=lengthInput;
                }
            } else {
                String preKeyword = Input.substring(idxBefore);
                KeywordMethod km = new KeywordMethod() {
                    @Override
                    public String Evaluate() {
                        return preKeyword;
                    }
                };
                Keyword kw = new Keyword(this, "", km);
                Replacement.add(kw);
                idxInput=lengthInput;
            }

        }
        return Replacement;
    }
    public void Clear(){
        Lock.Enter();
        try{
            Iterator<Keyword> it = iterator();
            while (it.hasNext()){
                Keyword kw = it.next();
                kw.Release();
            }
            clear();
        } finally {
            Lock.Leave();
        }
    }
    public String Stream(){
        StringBuilder sb = new StringBuilder();
        Lock.Enter();
        try{
            Iterator<Keyword> kwi = iterator();
            while (kwi.hasNext() ){
                Keyword k = kwi.next();
                sb.append(k.Builder.Evaluate());
            }
            return sb.toString();
        } finally{
            Lock.Leave();
        }
    }
    public Keywords(){
        Lock = new CriticalBlock();
    }

    public void Release(){
        Clear();
        Lock.Release();
        Lock=null;
    }

}
