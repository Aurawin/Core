package com.aurawin.core.log;

import com.aurawin.core.lang.Concat;
import com.aurawin.core.stream.FileStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class Syslog {
    private static String _charset = "UTF-8";
    private static String _delimit = "\t";
    private static String _end = "\r\n";
    public volatile FileStream _fs;
    public Syslog(String filename){
        try {
            _fs = new FileStream(filename, "rw");
        }catch (IOException e) {
            System.err.println("Notice: Unable to create log file "+filename);
        }
    }
    public synchronized void Append(String unit, String entryPoint, String message){
        if (_fs!=null) {
            String[] Data = new String[]{
                    new Timestamp(new java.util.Date().getTime()).toString(),
                    _delimit,
                    unit,
                    _delimit,
                    entryPoint,
                    _delimit,
                    message,
                    _end
            };
            try {
                _fs.write(Concat.toByteBuffer(Data));
            } catch (UnsupportedEncodingException e) {
                System.err.println("Notice: Unable to write UTF-8 log entry!");
            }
        } else {
            System.err.println("Notice: Unable to write any entries to disk!");

        }

    }
}
