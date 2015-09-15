package com.aurawin.core.rsr.def;

import com.aurawin.core.stream.MemoryStream;

public class Buffers {
    public MemoryStream Read;
    public MemoryStream Write;

    public Buffers() {
        Read = new MemoryStream();
        Write = new MemoryStream();
    }

    public void Reset(){
        Read.Clear();
        Write.Clear();
    }
    public void Release(){
        Reset();
        Read=null;
        Write=null;
    }


}
