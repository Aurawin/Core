package com.aurawin.core.stream.def;

public class StreamStats {
    public int collectionStart;
    public int collectionIndex;
    public long position;
    public long size;

    public StreamStats() {
        collectionIndex=0;
        collectionStart=0;
        position=0;
        size=0;
    }

    public void Reset(){
        collectionStart=0;
        collectionIndex=0;
        position=0;
        size=0;
    }
}
