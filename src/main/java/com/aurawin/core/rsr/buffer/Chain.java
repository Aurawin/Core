package com.aurawin.core.rsr.buffer;

import java.util.LinkedList;

/*
* This unit provides a list of buffers/files of various
* types to be assembled in a way such that the engine
* can contiguously send / receive data into memory or disk.
*
*
* */

public class Chain extends LinkedList<Item> {



    private long Size = 0;


    public long Capacity= 1024*1024*1024*2; // 2GB


    public long getSize(){
        return Size;
    }

}
