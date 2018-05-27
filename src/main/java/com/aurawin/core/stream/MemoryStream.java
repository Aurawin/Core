package com.aurawin.core.stream;


import com.aurawin.core.array.Bytes;
import com.aurawin.core.stream.def.StreamStats;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.LinkedList;

import static com.aurawin.core.lang.Table.CRLF;

public class MemoryStream implements SeekableByteChannel {

    protected StreamStats streamStats = new StreamStats();
    public static Integer MaxChunkSize = 1024*1024*5;

    protected volatile LinkedList<byte[]> Collection = new LinkedList<byte[]>();

    public MemoryStream(byte[] bytes){
        position(0);
    }
    public MemoryStream(){
        position(0);
    }
    public void Release(){
        Clear();
        Collection=null;
    }
    @Override
    public SeekableByteChannel truncate(long size){
        /*
         This will chop off data at a certain length;
        */
        if ( (size==0) || (size>size()) ) {
            Clear();
        } else {
            // todo truncate data
        }
        return this;
    }
    @Override
    public long size(){
        return streamStats.size;
    }
    @Override
    public long position(){
        return streamStats.position;
    }
    @Override
    public int write(ByteBuffer src){
        long Size = size();
        if (src.hasRemaining()==true){
            int iWrite=src.remaining();

            if (iWrite<=0)
                return 0;

            byte[] Chunk = null;
            byte[] baAppend = new byte[iWrite];
            src.get(baAppend, src.position(), iWrite);


            if (Collection.size()>=1) {
                Chunk = Collection.removeLast();
            }

            if  (Chunk!=null) {
                if  (Chunk.length+iWrite>MaxChunkSize) {
                    Collection.addLast(Chunk);// add prior remove
                    Collection.addLast(baAppend);
                    streamStats.size+=iWrite;
                } else {
                    byte [] baComb = new byte[Chunk.length+iWrite];
                    System.arraycopy(Chunk,0,baComb,0,Chunk.length);
                    System.arraycopy(baAppend,0,baComb,Chunk.length,iWrite);
                    Collection.addLast(baComb);
                    streamStats.size+=iWrite;
                }
            } else {
                Collection.add(baAppend);
                streamStats.size+=iWrite;
            }
            return iWrite;
        } else {
            return 0;
        }

    }
    @Override
    public SeekableByteChannel position(long newPosition){
        byte[] col;
        long size = streamStats.size;
        long iChunk=0;
        long iRemain=newPosition;
        streamStats.Reset();
        streamStats.size=size;
        while ( (streamStats.collectionIndex<Collection.size()) && (iRemain>0) ) {
            col = Collection.get(streamStats.collectionIndex);
            if (streamStats.collectionStart<col.length) {
                iChunk=(int) (col.length-streamStats.collectionStart);
                if (iChunk>iRemain)
                    iChunk=iRemain;
                if (iChunk>col.length)
                    iChunk=col.length;
                streamStats.collectionStart=(int) (streamStats.collectionStart+iChunk);

                iRemain-=iChunk;
            } else {
                streamStats.collectionStart=0;
                streamStats.collectionIndex++;
            }

        }
        streamStats.position=newPosition;
        return this;
    }

    @Override
    public int read(ByteBuffer dst){

        if (dst.hasRemaining()==true){
            position(0);

            int iRemain=dst.remaining();
            long iWrite=(size()-streamStats.position);

            int iChunk=0;
            int iTotal=0;



            byte[] col;

            while ( (streamStats.collectionIndex<Collection.size()) && (iWrite>0) && (iRemain>0) ) {
                col = Collection.get(streamStats.collectionIndex);
                if (streamStats.collectionStart<col.length) {
                    iChunk=(int) (col.length-streamStats.collectionStart);
                    if (iChunk>iRemain)
                        iChunk=iRemain;
                    if (iChunk>col.length)
                        iChunk=col.length;
                    if (iChunk>iWrite)
                        iChunk=(int) iWrite;
                    dst.put(col,streamStats.collectionStart,iChunk);
                    streamStats.collectionStart=streamStats.collectionStart+iChunk;
                    streamStats.position+=iChunk;
                    iRemain-=iChunk;
                    iWrite-=iChunk;
                    iTotal+=iChunk;
                } else {
                    streamStats.collectionStart=0;
                    streamStats.collectionIndex++;
                }
            }


            return iTotal;
        } else {
            return 0;
        }
    }
    @Override
    public boolean isOpen(){
        return true;
    }
    @Override
    public void close(){}

    public boolean hasRemaining(){
        return (streamStats.position!=streamStats.size);
    }
    public int Write (byte[] Value){
        byte[] itm = Value.clone();
        Collection.add(itm);
        streamStats.collectionIndex=Collection.size()-1;
        streamStats.collectionStart=itm.length;
        streamStats.size+=itm.length;
        streamStats.position+=itm.length;
        return itm.length;
    }
    public void SaveToFile(File Output) throws IOException{
        if (!Output.exists()) Output.createNewFile();
        BufferedOutputStream buffOut=new BufferedOutputStream(new FileOutputStream(Output));
        try {
            for (byte[] bytes : Collection)
                buffOut.write(bytes);
            buffOut.flush();
        } finally {
            buffOut.close();
        }
    }
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        String chunk;
        chunk = "Position : "+streamStats.position+CRLF;
        sb.append(chunk);
        chunk = "Size : "+streamStats.size+CRLF;
        sb.append(chunk);
        chunk = "Index : "+streamStats.collectionIndex+CRLF;
        sb.append(chunk);
        chunk = "Start : "+streamStats.collectionStart+CRLF;
        sb.append(chunk);

        return sb.toString();
    }
    public void LoadFromFile(File File) throws IOException{
        streamStats.Reset();
        Collection.clear();
        FileInputStream is = new FileInputStream(File);
        try {
            Write(is);
        } finally{
            is.close();
        }
    }
    public long calculateSize(){
        long size =0 ;
        for (byte[] b : Collection) size+=b.length;
        return size;
    }

    public void Write (InputStream Value) throws IOException{
        int iWrite=0;
        byte[] baBuffer=new byte[1024*1024];
        byte[] Chunk = null;
        BufferedInputStream bfi= new BufferedInputStream(Value);
        try {
            while (bfi.available()>0) {
                iWrite = bfi.read(baBuffer);//Value.read(baBuffer);
                if (iWrite > -1) {
                    byte[] baAppend = new byte[iWrite];
                    System.arraycopy(baBuffer, 0, baAppend, 0, iWrite);

                    Chunk = (Collection.size() >= 1) ? Collection.removeLast() : null;

                    if (Chunk != null) {
                        if  (Chunk.length + iWrite > MaxChunkSize) {
                            Collection.addLast(Chunk);
                            Collection.addLast(baAppend);
                        } else {
                            byte[] baComb = new byte[Chunk.length + iWrite];
                            System.arraycopy(Chunk, 0, baComb, 0, Chunk.length);
                            System.arraycopy(baAppend, 0, baComb, Chunk.length, iWrite);
                            Collection.addLast(baComb);
                        }
                    } else {
                        Collection.addLast(baAppend);
                    }
                }
            }
            streamStats.Reset();
            streamStats.size=calculateSize();
        } finally{
            bfi.close();
        }
    }
    public int Write (long Value){

         byte[] itm = new byte[] {
            (byte) (Value >> 56),
            (byte) (Value >> 48),
            (byte) (Value >> 40),
            (byte) (Value >> 32),
            (byte) (Value >> 24),
            (byte) (Value >> 16),
            (byte) (Value >> 8),
            (byte) Value
        };
        Collection.add(itm);
        streamStats.size+=itm.length;

        return itm.length;
    }
    public  int Write (int Value){
        byte[] itm = new byte[] {
                (byte) (Value >> 32),
                (byte) (Value >> 24),
                (byte) (Value >> 16),
                (byte) (Value >> 8),
                (byte) Value
        };
        Collection.add(itm);
        streamStats.size+=itm.length;
        return itm.length;
    }
    public  byte[] Read(int Count){
        return Read(streamStats.position,Count,false);
    }
    public  byte[] Read(){
        return Read(streamStats.position,size(),false);
    }
    public  long readWhole(int Count){
        byte[] ba = Read(Count);
        long result = 0;
        for (int i = 0; i < ba.length; i++)
        {
            result = (result << 8) + (ba[i] & 0xff);
        }

        return result;

    }
    public  String readString(int count, String charset){
        try {
            return new String(Read(count), charset);
        } catch (UnsupportedEncodingException iee){
            return "";
        }

    }

    public  String readStringUntil(byte until,long position, String charset){

        long idx = Find(new byte[]{Byte.valueOf(until)},position);

        int count = (int) (idx - position);
        position(position);

        return readString(count, charset);

    }
    public  byte readByte(){
        byte[] ba = Read(1);
        return ba[0];
    }
    public  int readInteger(){
        return (int) readWhole(4);
    }
    public  double readDecimal(int Count){
        byte[] ba = Read(Count);
        double result = ba[0];

        for (int iLcv=1; iLcv<Count; iLcv++){
            result = (ba[iLcv] & 0xFF) << iLcv;
        }
        return result;

    }
    public  byte[] Read(long Offset,long Count, boolean Peak){
        long OldPosition=position();
        position(Offset);

        long iChunk=0;
        long iTotal=0;
        long iRead=Count;
        byte[] col;
        byte[] Result = new byte[(int) Count];

        // seek to Collection with position
        while ( (streamStats.collectionIndex<Collection.size()) && (iRead>0) ) {
            col = Collection.get(streamStats.collectionIndex);
            iChunk=col.length-streamStats.collectionStart;
            if (iChunk>iRead)
                iChunk=iRead;
            System.arraycopy(col, streamStats.collectionStart, Result,(int)iTotal , (int) iChunk);
            streamStats.position+=iChunk;
            iRead-=iChunk;
            iTotal+=iChunk;
            streamStats.collectionIndex++;
            streamStats.collectionStart=0;
        }
        if (Peak==true) position(OldPosition);
        return Result;
    }
    public  int Write (boolean Value){
        byte[] itm = new byte[1];
        itm[0]=(Value==true) ? (byte) 1 : (byte) 0;
        Collection.add(itm);
        streamStats.size+=itm.length;
        return itm.length;
    }
    public  int Write (String Value){
        byte[] itm = Value.getBytes();
        Collection.add(itm);

        streamStats.size+=itm.length;
        return itm.length;
    }



    public  void Clear() {
        while (Collection.size() > 0) {
            Collection.pop();
        }
        streamStats.Reset();
    }
    public  void sliceAtPosition(){
        if (streamStats.position>0) {
            int iLcv=0;
            int iChunk=0;
            byte[] baPOP;
            byte[] baChunk;

            if (streamStats.collectionIndex<Collection.size()){
                while (iLcv<streamStats.collectionIndex){
                    Collection.pop();
                    iLcv++;
                }
                baPOP = Collection.pop();
                iChunk = baPOP.length - streamStats.collectionStart;
                baChunk = new byte[iChunk];
                System.arraycopy(baPOP, streamStats.collectionStart, baChunk, 0, iChunk);
                Collection.addFirst(baChunk);
            }
        }
        streamStats.Reset();
        streamStats.size=calculateSize();
    }
    public  void Move(MemoryStream dest, long length){
        dest.Clear();
        sliceAtPosition();
        if (length>0) {
            int iLcv=0;
            int iChunk=0;
            int iRemain=0;
            long iTotal=0;
            byte[] baPOP;
            byte[] baChunk;
            byte[] baRemain;

            while ((Collection.size()>0) && (iTotal<length) ){
                baPOP = Collection.pop();
                iChunk = baPOP.length;
                if ((iChunk+iTotal)>length) {
                    iRemain=iChunk-(int) (length-iTotal);
                    iChunk = iChunk-iRemain;
                    iTotal+=iChunk;
                    baChunk=new byte[iChunk];
                    baRemain=new byte[iRemain];
                    System.arraycopy(baPOP, 0, baChunk, 0, iChunk); // copy bits before in POP
                    System.arraycopy(baPOP, iChunk, baRemain, 0, iRemain);
                    dest.Collection.add(baChunk);
                    Collection.addFirst(baRemain); // leftovers remain in original collection

                    baRemain=new byte[iRemain];


                } else {
                    dest.Collection.add(baPOP);
                }
            }
            dest.streamStats.Reset();
            dest.streamStats.size=dest.calculateSize();

            streamStats.Reset();
            streamStats.size=calculateSize();

        }
    }
    public  void Move(MemoryStream Dest){
        sliceAtPosition();
        while (Collection.size() >0 ) {
            byte[] itm = Collection.pop();
            Dest.Collection.add(itm);

            Dest.streamStats.Reset();
            Dest.streamStats.size=Dest.calculateSize();
        }
        streamStats.Reset();
    }

    public  void CopyFrom(MemoryStream Source){
        position(streamStats.size);
        if (Source!=null) {
            Source.Collection.stream()
                    .forEach(ba->Collection.add(ba.clone()));
        }
    }
    public  long Find(byte[] Term, long position){
        long iPreSeek=0;
        long iSeek = position;
        long iResult = -1;
        int iLcv =0;
        int idxTerm=-1;

        int iColPosition=0;
        int iTermLen=Term.length;

        byte[] bWindow = null;
        byte[] col = null;

        while ( (iLcv<Collection.size()) && (iSeek<streamStats.size) ) {
            col = Collection.get(iLcv);
            if (iPreSeek+col.length>=position) {
                iColPosition =(int) (position - iPreSeek);
                idxTerm = Bytes.indexOf(col, Term,(int) iColPosition, 0);
                if (idxTerm > -1) {
                    iResult = idxTerm + iPreSeek;
                    break;
                } else {
                    iSeek += col.length;
                    iLcv++;
                }

            } else {
                iPreSeek+=col.length;
            }
        }
        return iResult;

    }
    public  long Find(String Term, long position){
        try {
            return Find(Term.getBytes("UTF-8"),position);

        } catch (UnsupportedEncodingException uee){
            return -1;
        }

    }

    public  long Find(String Term){
        return Find(Term, streamStats.position);
    }

}
