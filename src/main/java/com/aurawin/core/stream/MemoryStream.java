package com.aurawin.core.stream;


import com.aurawin.core.array.Bytes;
import com.aurawin.core.stream.def.ReadStats;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import static com.aurawin.core.lang.Table.CRLF;

public class MemoryStream extends Channel {
    private long Position;
    private ReadStats readStats = new ReadStats();
    public static Integer MaxChunkSize = 1024*1024*5;

    protected volatile LinkedList<byte[]> Collection = new LinkedList<byte[]>();

    public MemoryStream(byte[] bytes){
        Size=0;
        position(0);
    }
    public MemoryStream(){
        Size=0;
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
        if ( (size==0) || (size>Size) ) {
            Clear();
        } else {
            // todo truncate data
        }
        return this;
    }
    @Override
    public long size(){
        long size =0 ;
        for (byte[] b : Collection) size+=b.length;
        return size;
    }
    @Override
    public long position(){
        return Position;
    }
    @Override
    public int write(ByteBuffer src){

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
                } else {
                    byte [] baComb = new byte[Chunk.length+iWrite];
                    System.arraycopy(Chunk,0,baComb,0,Chunk.length);
                    System.arraycopy(baAppend,0,baComb,Chunk.length,iWrite);
                    Collection.addLast(baComb);
                }
            } else {
                Collection.add(baAppend);
            }
            Size+=iWrite;

            return iWrite;
        } else {
            return 0;
        }

    }
    @Override
    public SeekableByteChannel position(long newPosition){
        readStats.Reset();
        byte[] col;
        long iChunk=0;
        long iRemain=newPosition;

        while ( (readStats.collectionIndex<Collection.size()) && (iRemain>0) ) {
            col = Collection.get(readStats.collectionIndex);
            if (readStats.collectionStart<col.length) {
                iChunk=(int) (col.length-readStats.collectionStart);
                if (iChunk>iRemain)
                    iChunk=iRemain;
                if (iChunk>col.length)
                    iChunk=col.length;
                readStats.collectionStart=(int) (readStats.collectionStart+iChunk);
                iRemain-=iChunk;
            } else {
                readStats.collectionStart=0;
                readStats.collectionIndex++;
            }

        }
        Position=newPosition;
        return this;
    }

    @Override
    public int read(ByteBuffer dst){
        if (dst.hasRemaining()==true){
            int iRemain=dst.remaining();
            long iWrite=(Size-Position);

            int iChunk=0;
            int iTotal=0;



            byte[] col;

            while ( (readStats.collectionIndex<Collection.size()) && (iWrite>0) && (iRemain>0) ) {
                col = Collection.get(readStats.collectionIndex);
                if (readStats.collectionStart<col.length) {
                    iChunk=(int) (col.length-readStats.collectionStart);
                    if (iChunk>iRemain)
                        iChunk=iRemain;
                    if (iChunk>col.length)
                        iChunk=col.length;
                    if (iChunk>iWrite)
                        iChunk=(int) iWrite;
                    dst.put(col,readStats.collectionStart,iChunk);
                    readStats.collectionStart=readStats.collectionStart+iChunk;
                    Position+=iChunk;
                    iRemain-=iChunk;
                    iWrite-=iChunk;
                    iTotal+=iChunk;
                } else {
                    readStats.collectionStart=0;
                    readStats.collectionIndex++;
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
        return (Size!=0) && (Position!=Size);
    }
    public int Write (byte[] Value){
        byte[] itm = Value.clone();
        Collection.add(itm);
        Size+=itm.length;

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
        chunk = "Position : "+Position+CRLF;
        sb.append(chunk);
        chunk = "Size : "+Size+CRLF;
        sb.append(chunk);

//        for(byte[] ba:Collection){
//            chunk=new String(ba);
//            sb.append(chunk);
//        }

        return sb.toString();
    }
    public void LoadFromFile(File File) throws IOException{
        Collection.clear();
        FileInputStream is = new FileInputStream(File);
        try {
            Write(is);
        } finally{
            is.close();
        }
    }
    public long calculateSize(){
        return size();
    }

    public long Write (InputStream Value) throws IOException{
        int iWrite=0;
        byte[] baBuffer=new byte[1024*1024];
        BufferedInputStream bfi= new BufferedInputStream(Value);
        try {
            while (bfi.available()>0) {
                iWrite = bfi.read(baBuffer);//Value.read(baBuffer);
                if (iWrite > -1) {
                    byte[] baAppend = new byte[iWrite];
                    System.arraycopy(baBuffer, 0, baAppend, 0, iWrite);
                    byte[] Chunk = null;
                    if (Collection.size() >= 1) {
                        Chunk = Collection.removeLast();
                    }
                    if ((Chunk != null) && (Chunk.length + iWrite > MaxChunkSize)) {
                        Collection.add(baAppend);
                    } else if (Chunk == null) {
                        Collection.add(baAppend);
                    } else {
                        byte[] baComb = new byte[Chunk.length + iWrite];
                        System.arraycopy(Chunk, 0, baComb, 0, Chunk.length);
                        System.arraycopy(baAppend, 0, baComb, Chunk.length, iWrite);
                        Collection.add(baComb);
                    }
                    Size += iWrite;
                }
            }
            return Size;
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
        Size+=itm.length;

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
        Size+=itm.length;

        return itm.length;
    }
    public  byte[] Read(int Count){
        return Read(Position,Count,false);
    }
    public  byte[] Read(){
        return Read(Position,Size,false);
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
        Position = position;

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
        long OldPosition=Position;
        Position=Offset;

        long iPreSeek=0;
        int iOffset=0;
        long iChunk=0;
        int iTotal=0;
        int iColSize=0;
        long iRead=Count;
        int iLcv =0;
        byte[] col;



        byte[] Result = new byte[(int) Count];

        // seek to Collection with position
        while ( (iLcv<Collection.size()) && (iRead>0) ) {
            col = Collection.get(iLcv);
            iColSize=col.length;
            if (iPreSeek+iColSize>=Position) {
                iOffset=(int)(Position-iPreSeek);
                iChunk=iColSize-iOffset;
                if (iChunk>iRead)
                    iChunk=iRead;
                System.arraycopy(col, iOffset, Result, iTotal, (int) iChunk);

                Position+=iChunk;

                iRead-=iChunk;
                iTotal+=iChunk;
            }
            iPreSeek+=iColSize;
            iLcv++;
        }
        if (Peak==true) Position=OldPosition;
        return Result;
    }
    public  int Write (boolean Value){
        byte[] itm = new byte[1];
        itm[0]=(Value==true) ? (byte) 1 : (byte) 0;
        Collection.add(itm);
        Size+=itm.length;

        return itm.length;
    }
    public  int Write (String Value){
        byte[] itm = Value.getBytes();
        Collection.add(itm);
        Size+=itm.length;
        return itm.length;
    }



    public  void Clear() {
        // seek to Collection with position

        while (Collection.size() > 0) {
            byte[] itm = Collection.pop();
            itm = null;
        }
        Position=0;
        Size=0;
    }
    public  void sliceAtPosition(){
        readStats.Reset();
        if (Position>0) {
            int iLcv = 0;
            int iColSize = 0;

            long iPreSeek = 0;
            int iOffset = 0;
            int iChunk = 0;

            while ((iLcv < Collection.size()) && (Position <= Size)) {
                iColSize = Collection.get(iLcv).length;
                if (iPreSeek + iColSize >= Position) {
                    // this array is the current []
                    iOffset = (int) (Position - iPreSeek);
                    iChunk = iColSize - iOffset;
                    if (iChunk > 0) {
                        byte[] baChunk = new byte[iChunk];
                        System.arraycopy(Collection.get(iLcv), iOffset, baChunk, 0, iChunk);
                        Collection.set(iLcv, baChunk);
                        iLcv = Collection.size();
                    } else {
                        Collection.remove(iLcv);
                    }

                } else {
                    Collection.remove(iLcv);
                }
                iPreSeek += iColSize;
            }
            Size = size();
            Position = 0;
        }
    }
    public  void Move(MemoryStream dest, long length){
        sliceAtPosition();
        dest.Clear();
        if (length>0) {
            int iLcv = 0;
            int iColSize = 0;
            int iChunk = 0;
            long iMoved = 0;

            while ((iLcv < Collection.size()) && (Position < Size) && (iMoved < length)) {
                iColSize = Collection.get(iLcv).length;
                iChunk = iColSize;
                if ((iChunk + iMoved) > length) iChunk = (int) (length - iMoved);
                if (iChunk > 0) {
                    byte[] baChunk = new byte[iChunk];
                    System.arraycopy(Collection.get(iLcv), 0, baChunk, 0, iChunk);
                    dest.Write(baChunk);
                    iMoved += iChunk;
                    iLcv++;
                    Position += iChunk;
                } else {
                    iLcv++;
                }
            }
            sliceAtPosition();
        }
    }
    public  long Move(MemoryStream Dest){
        sliceAtPosition();
        while (Collection.size() >0 ) {
            byte[] itm = Collection.pop();
            Dest.Collection.add(itm);
            Dest.Size+=itm.length;
        }
        Size=0;
        return Dest.Size;
    }

    public  long CopyFrom(MemoryStream Source){
        Position=Size;
        if (Source!=null) {
            Source.Collection.stream()
                    .forEach(ba->Collection.add(ba.clone()));
            Size = Size+Source.Size;
        }
        return Size;
    }
    public  long Find(byte[] Term, long position){
        long iPreSeek=0;
        int iOffset=0;
        int iChunk=0;
        long iSeek = position;
        long iResult = -1;
        int iLcv =0;
        int idxTerm=-1;
        int iColSize=0;
        int iColPosition=0;
        int iTermLen=Term.length;

        byte[] bWindow = null;
        byte[] col = null;

        while ( (iLcv<Collection.size()) && (iSeek<Size) ) {
            col = Collection.get(iLcv);
            if (iPreSeek+col.length>=Position) {
                iColPosition =(int) (position - iPreSeek);
                idxTerm = Bytes.indexOf(col, Term,(int) iColPosition, 0);
                if (idxTerm > -1) {
                    iResult = idxTerm + iPreSeek;
                    break;
                } else {
                    iSeek += iColSize;
                    iLcv++;
                }

            } else {
                iPreSeek+=iColSize;
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
        return Find(Term, Position);
    }

}
