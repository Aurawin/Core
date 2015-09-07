package com.aurawin.core.file;


import com.aurawin.core.stream.FileStream;

import java.io.File;
import java.io.IOException;

public class Reader {
    public static String toString(File f){
        try {
            FileStream fs = new FileStream(f, "r");
            try {
                return fs.toString();
            } finally {
                fs.close();
            }
        } catch (IOException e){
            return "";
        }


    }
}
