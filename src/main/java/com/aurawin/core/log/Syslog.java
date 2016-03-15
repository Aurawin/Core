package com.aurawin.core.log;

import com.aurawin.core.lang.Concat;
import com.aurawin.core.lang.Table;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.FileStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class Syslog {
    private static String _charset = "UTF-8";
    private static String _delimit = "\t\t";
    private static String _end = System.getProperty("line.separator");
    private static volatile FileStream _fs;

    public Syslog() throws Exception{
        checkBase();
        createFile(Settings.File.Log.Path());
    }
    public Syslog(String filename)throws Exception{
        checkBase();
        createFile(filename);
    }
    public static void createFile(String filename) throws Exception{
        if (_fs!=null){
            _fs.close();
        }
        _fs = new FileStream(filename, "rw");
    }
    private static void checkBase() throws Exception{
        java.io.File Path = new java.io.File(Settings.File.Log.Base());
        if (!Path.exists()) Path.mkdirs();
    }
    public static void main(String[] args) throws Exception {
//        Settings.Initialize("com.aurawin.core");
        Syslog log = new Syslog();
        Append("Syslog","main","test");
        log.Release();
    }
    public static synchronized void Append(String unit, String entryPoint, String message){
        if (_fs==null){
            try {
                checkBase();
                createFile(Settings.File.Log.Path());
            } catch (Exception e){
                try {
                    System.err.println(Table.Exception.Syslog.Notice(Table.Exception.Syslog.UnableToCreateDefaultLogfile, Settings.File.Log.Path()));
                } catch (Exception e1){
                    // do nothing
                }
            }
        }
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
                _fs.Position=_fs.Size;
                _fs.write(Concat.toByteBuffer(Data));
            } catch (UnsupportedEncodingException e) {
                System.err.println(Table.Exception.Syslog.Notice(Table.Exception.Syslog.UnableToWriteEntry));
            }
        } else {
            System.err.println(Table.Exception.Syslog.Notice(Table.Exception.Syslog.UnableToWriteEntries));
        }

    }
    public synchronized void Release(){
        if (_fs!=null) _fs.close();
        _fs=null;
    }
}
