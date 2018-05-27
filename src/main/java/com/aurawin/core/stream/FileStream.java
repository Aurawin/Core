package com.aurawin.core.stream;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;

public class FileStream implements SeekableByteChannel {
    private long Position;

    private RandomAccessFile Data;

    public FileStream(File f, String mode) throws IOException{
        Data = new RandomAccessFile(f, mode);
        position(Data.getFilePointer());
    }
    public FileStream(String name, String mode) throws IOException{
        Data = new RandomAccessFile(name, mode);
        Position=Data.getFilePointer();
    }
    public SeekableByteChannel truncate(long size){
        FileChannel ch = Data.getChannel();
        try {
            ch.truncate(size);
            return ch;
        } catch(IOException e){
            return this;
        }
    }

    public long size()   {
        try {
            return Data.length();
        } catch (IOException ioe){
            return 0;
        }
    }
    public long position(){
        return Position; // todo
    }
    public int write(byte[] data){
        FileChannel ch = Data.getChannel();
        int iCount=0;
        try {
            ch.position(Position);
            try {
                ByteBuffer src = ByteBuffer.allocate(data.length);
                src.put(data);
                src.flip();
                while (src.hasRemaining()) {
                    iCount += ch.write(src);
                }
            } catch (IOException e) {
                // todo
                iCount = -1;
            }
            Position = ch.position();
            return iCount;
        } catch (IOException e){
            return 0;
        }
    }
    public int write(ByteBuffer src){
        // write entire to
        FileChannel ch = Data.getChannel();
        int iCount=0;
        try {
            ch.position(Position);
            try {
                while (src.hasRemaining()) {
                    iCount += ch.write(src);
                }
            } catch (IOException e) {
                // todo
                iCount = -1;
            }
            Position = ch.position();
            return iCount;
        } catch (IOException e){
            return 0;
        }
    }
    public SeekableByteChannel position(long newPosition){
        Position=newPosition;
        return this;
    }
    public int read(ByteBuffer dst){
        int iCount=0;
        FileChannel ch = Data.getChannel();
        try {
            ch.position(Position);
            iCount+=ch.read(dst);
            Position=ch.position();
        } catch (IOException e){
            // todo
            iCount=-1;
        }
        return iCount;
    }
    public int Write (String s){
        return write(ByteBuffer.wrap(s.getBytes()));
    }

    public boolean isOpen(){
        return (Data!=null);
    }
    public void close(){
        if (Data!=null) {
            try {
                Data.close();
            } catch (IOException e){

            }
            Data=null;
        }
    }

    public String toString(){
        String Result="";
        try {
            Data.seek(0);
            String Line = Data.readLine();
            while (Line != null) {
                Result += Line + "\n";
                Line = Data.readLine();
            }
            return Result;
        } catch (IOException e){
            return "";
        }
    }
}
