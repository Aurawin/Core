package com.aurawin.core.stream.def;

public class ReadStats {
    public int collectionStart;
    public int collectionIndex;

    public ReadStats() {
        collectionIndex=0;
        collectionStart=0;
    }

    public void Reset(){
        collectionStart=0;
        collectionIndex=0;
    }
}
