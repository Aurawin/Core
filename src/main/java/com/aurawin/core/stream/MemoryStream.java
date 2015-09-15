package com.aurawin.core.stream;


import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.LinkedList;

public class MemoryStream extends Channel {

    private LinkedList<byte[]> Collection = new LinkedList<byte[]>();

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
            byte[] Chunk= new byte[iWrite];
            src.get(Chunk,src.position(),iWrite);
            src.clear();
            Collection.add(Chunk);
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
        return Read(iOffset,Count);
    }
    public synchronized byte[] Read(){
        int iOffset=0;
        int  iCount=(int) Size;
        return Read(iOffset,iCount);
    }
    public synchronized byte[] Read(int Offset,int Count){
        long OldPosition=Position;
        Position=Offset;

        long iPreSeek=0;
        int iOffset=0;
        int iChunk=0;
        int iTotal=0;
        int iColSize=0;
        int iWrite=Count;
        int iLcv =0;



        byte[] Result = new byte[(int) (Size-Position)];

        // seek to Collection with position


        while ( (iLcv<Collection.size()) && (iWrite>0) ) {
            iColSize=Collection.get(iLcv).length;
            if (iPreSeek+iColSize>=Position) {
                iOffset=(int)(Position-iPreSeek);
                iChunk=iColSize-iOffset;

                System.arraycopy(Collection.get(iLcv), (int) iOffset, Result, iTotal, iChunk);

                Position+=iChunk;

                iWrite-=iChunk;
                iTotal+=iChunk;
            }
            iPreSeek+=iColSize;
            iLcv++;
        }

        Position=OldPosition;
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

        while ( (iLcv<Collection.size()) && (Position>0) ) {
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

}
