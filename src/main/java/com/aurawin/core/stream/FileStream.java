package com.aurawin.core.stream;


import javafx.geometry.Pos;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;

public class FileStream extends base {
    private RandomAccessFile Data;

    public FileStream(File f, String mode) throws IOException{
        Data = new RandomAccessFile(f, mode);
        Size=Data.length();
        Position=Data.getFilePointer();
    }
    public FileStream(String name, String mode) throws IOException{
        Data = new RandomAccessFile(name, mode);
        Size=Data.length();
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

    public long size(){
        return Size; //todo
    }
    public long position(){
        return Position; // todo
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
            Size = ch.size();
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
            Size=ch.size();
            Position=ch.position();
        } catch (IOException e){
            // todo
            iCount=-1;
        }
        return iCount;
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
