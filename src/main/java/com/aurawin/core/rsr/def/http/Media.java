package com.aurawin.core.rsr.def.http;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class Media {
    public enum State {None,Idle,Query,Established}
    public class Range {
        public volatile long Start = 0;
        public volatile long Stop = 0;
        public volatile long Size = 0;

        public Range(long Start, long Stop, long Size){
            this.Start=Start;
            this.Stop=Stop;
            this.Size=Size;
        }
    }
    public class Manifest {
        public volatile Media.State State;
        public volatile RandomAccessFile File;
        public volatile FileChannel Channel;
        public volatile Media.Range Range;
        public volatile float Modified;
        public volatile String ETag;
        public volatile String ContentType;
        public volatile long ContentLength;
        public volatile String URI;
        public Manifest (Media.State State,RandomAccessFile File, FileChannel Channel,
                         Media.Range Range, float Modified,String ETag,String ContentType, long ContentLength,
                         String URI){
            this.State=State;
            this.File=File;
            this.Channel=Channel;
            this.Range=Range;
            this.Modified=Modified;
            this.ETag=ETag;
            this.ContentType=ContentType;
            this.ContentLength=ContentLength;
            this.URI=URI;
        }
    }
}
