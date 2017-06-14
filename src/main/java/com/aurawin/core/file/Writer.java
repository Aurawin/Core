package com.aurawin.core.file;


import com.aurawin.core.stream.FileStream;
import com.aurawin.core.stream.MemoryStream;

import java.io.File;
import java.io.IOException;

public class Writer {

    public static void toFile(MemoryStream Data, File Output) throws IOException{
        Data.SaveToFile(Output);
    }
}
