package com.aurawin.core.stream;


import com.aurawin.core.array.Bytes;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.LinkedList;

public class MemoryStream extends Channel {
    public static Integer MaxChunkSize = 1024*1024*512;

    protected LinkedList<byte[]> Collection = new LinkedList<byte[]>();

    public MemoryStream(byte[] bytes){
        Size=0;
        Position=0;
    }
    public MemoryStream(){
        Size=0;
        Position=0;
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
        // write entire to
        if (src.hasRemaining()==true){
            int iWrite=src.remaining();


            byte[] Chunk = null;
            byte[] baAppend = new byte[iWrite];
            src.get(baAppend, src.position(), iWrite);


            if (Collection.size()>=1) {
                Chunk = Collection.removeLast();
            }

            if ( (Chunk!=null) && (Chunk.length+iWrite>MaxChunkSize)) {
                Collection.add(baAppend);
            } else if (Chunk==null){
                Collection.add(baAppend);
            } else {
                byte [] baComb = new byte[Chunk.length+iWrite];
                System.arraycopy(Chunk,0,baComb,0,Chunk.length);
                System.arraycopy(baAppend,0,baComb,Chunk.length,iWrite);

                Collection.add(baComb);
            }



            Size+=iWrite;
            return iWrite;
        } else {
            return 0;
        }

    }
    @Override
    public SeekableByteChannel position(long newPosition){
        Position=newPosition;
        return this;
    }
    @Override
    public int read(ByteBuffer dst){
        if (dst.hasRemaining()==true){
            long iPreSeek=0;
            long iOffset=0;

            int iRemain=dst.remaining();
            int iChunk=0;
            int iTotal=0;
            int iColSize=0;
            long iWrite=(Size-Position);
            int iLcv =0;
            // seek to Collection with position
            while ( (iLcv<Collection.size()) && (iWrite>0) && (iRemain>0) ) {
                iColSize=Collection.get(iLcv).length;
                if (iPreSeek+iColSize>=Position) {
                    iOffset=(Position-iPreSeek);
                    iChunk=iColSize-(int) iOffset;
                    if (iChunk>iRemain)
                        iChunk=iRemain;
                    dst.put(Collection.get(iLcv),(int)iOffset,iChunk);
                    Position+=iChunk;
                    iRemain-=iChunk;
                    iWrite-=iChunk;
                    iTotal+=iChunk;
                }
                iPreSeek+=iColSize;
                iLcv++;
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

    public synchronized int Write (byte[] Value){
        byte[] itm = Value.clone();
        Collection.add(itm);
        Size+=itm.length;

        return itm.length;
    }
    public synchronized int Write (InputStream Value) throws IOException{
        byte[] baBuffer=new byte[1024*1024];
        BufferedInputStream bfi= new BufferedInputStream(Value);

        int iWrite=bfi.read(baBuffer);//Value.read(baBuffer);
        if (iWrite>-1) {
            byte[] baAppend = new byte[iWrite];
            System.arraycopy(baBuffer, 0, baAppend, 0,iWrite);
            byte[] Chunk=null;
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
        return iWrite;
    }
    public synchronized int Write (long Value){

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
    public synchronized int Write (int Value){
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
    public synchronized byte[] Read(int Count){
        int iOffset=0;
        return Read(iOffset,Count,false);
    }
    public synchronized byte[] Read(){
        int iOffset=0;
        int  iCount=(int) Size;
        return Read(iOffset,iCount,false);
    }

    public synchronized byte[] Read(int Offset,int Count, boolean Peak){
        long OldPosition=Position;
        Position=Offset;

        long iPreSeek=0;
        int iOffset=0;
        int iChunk=0;
        int iTotal=0;
        int iColSize=0;
        int iRead=Count;
        int iLcv =0;



        byte[] Result = new byte[(int) (Size-Position)];

        // seek to Collection with position


        while ( (iLcv<Collection.size()) && (iRead>0) ) {
            iColSize=Collection.get(iLcv).length;
            if (iPreSeek+iColSize>=Position) {
                iOffset=(int)(Position-iPreSeek);
                iChunk=iColSize-iOffset;
                if (iChunk>iRead)
                    iChunk=iRead;
                System.arraycopy(Collection.get(iLcv), iOffset, Result, iTotal, iChunk);

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
    public synchronized int Write (boolean Value){
        byte[] itm = new byte[1];
        itm[0]=(Value==true) ? (byte) 1 : (byte) 0;
        Collection.add(itm);
        Size+=itm.length;

        return itm.length;
    }
    public synchronized int Write (String Value){
        byte[] itm = Value.getBytes();
        Collection.add(itm);
        Size+=itm.length;
        return itm.length;
    }

    public synchronized long Move(MemoryStream Value){
        while (Value.Collection.size() >0 ) {
            byte[] itm = Value.Collection.pop();
            Collection.add(itm);
            Size+=itm.length;
        }
        Value.Clear();

        return Size;
    }

    public synchronized void Clear() {
        // seek to Collection with position

        while (Collection.size() > 0) {
            byte[] itm = Collection.pop();
            itm = null;
        }
        Position=0;
        Size=0;
    }
    public synchronized void sliceAtPosition(){
        int iLcv =0;
        int iColSize=0;

        long iPreSeek=0;
        int iOffset=0;
        int iChunk=0;

        while ( (iLcv<Collection.size()) && (Position<=Size) ) {
            iColSize=Collection.get(iLcv).length;
            if (iPreSeek+iColSize>=Position) {
                // this array is the current []
                iOffset=(int)(Position-iPreSeek);
                iChunk=iColSize-iOffset;
                if (iChunk>0) {
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
            iPreSeek+=iColSize;
        }
        Size=size();
        Position=0;
    }
    public synchronized void Move(MemoryStream dest, long length){
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
    public synchronized long Find(String Term){
        long iPreSeek=0;
        int iOffset=0;
        int iChunk=0;
        long iSeek = Position;
        long iResult = -1;
        int iLcv =0;
        int idxTerm=-1;
        int iColSize=0;
        int iTermLen=0;
        byte[] bTerm = null;
        byte[] bWindow = null;
        byte[] col = null;
        try {
            bTerm=Term.getBytes("UTF-8");
            iTermLen=bTerm.length;
        } catch (UnsupportedEncodingException uee){
            return iResult;
        }

        while ( (iLcv<Collection.size()) && (iSeek<Size) ) {
            col = Collection.get(iLcv);
            if (iPreSeek+col.length>=Position) {
                idxTerm = Bytes.indexOf(Collection.get(iLcv), bTerm);
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

}
