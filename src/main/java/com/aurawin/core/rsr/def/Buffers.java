package com.aurawin.core.rsr.def;


import com.aurawin.core.stream.MemoryStream;

public class Buffers {
    public MemoryStream Recv;
    public MemoryStream Send;

    public Buffers() {
        Recv = new MemoryStream();
        Send = new MemoryStream();
    }

    public void Reset(){
        Recv.Clear();
        Send.Clear();
    }
    public void Release(){
        Reset();
        Recv=null;
        Send=null;
    }


}
