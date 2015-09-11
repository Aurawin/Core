package com.aurawin.core.file;

import java.io.File;

public class Util {
    public static String extractFileName(String filename, ExtractOption option){
        int idxPath=filename.lastIndexOf(File.pathSeparator);
        if (idxPath==-1) idxPath=filename.length();
        filename=filename.substring(0,idxPath-1);
        switch (option) {
           case eoName:
               int idxExt=filename.lastIndexOf(".");
               if (idxExt==-1) idxExt=filename.length()+1;
               return filename.substring(idxPath+1,idxExt-1);
           case eoNameAndExtension :
               return filename;
        }
        return filename;

    }
}
