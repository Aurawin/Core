package com.aurawin.core.solution;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.json.Builder;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.json.JSONObject;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.aurawin.core.lang.Table.CRLF;
import static com.aurawin.core.solution.Settings.Resource.MaxBufferSize;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Version {
    public static final String defaultResource = "/buildNumber";

    @Expose(serialize = true, deserialize = true)
    public long Major;
    @Expose(serialize = true, deserialize = true)
    public long Mid;
    @Expose(serialize = true, deserialize = true)
    public long Minor;
    @Expose(serialize = true, deserialize = true)
    public long Build;
    public String toString(){
        return Major + "."+ Mid+"."+Minor+"."+Build;
    }
    public void loadFromResource(String resource) throws IOException{
        InputStream is = getClass().getResourceAsStream(defaultResource);
        InputStreamReader ir = new InputStreamReader(is, UTF_8);
        BufferedReader r = new BufferedReader(ir);
        StringBuilder sb = new StringBuilder(MaxBufferSize);
        String sLine = "";
        while ((sLine = r.readLine()) != null) {
            sb.append(sLine);
            sb.append(CRLF);
        }

        KeyPairs kpl = new KeyPairs();
        kpl.DelimiterItem=CRLF;
        kpl.DelimiterField="=";
        kpl.Load(sb.toString());

        Major = kpl.ValueAsLong("buildYear",0);
        Mid = kpl.ValueAsLong("buildMonth",0);
        Minor = kpl.ValueAsLong("buildDay",0);
        Build = kpl.ValueAsLong("buildNumber",0);
    }

    public void loadFromResouce() throws IOException {
        loadFromResource(defaultResource);
    }

    private void load(String Data){

        Builder bldr = new Builder();
        Gson gs = bldr.Create();

        Assign(gs.fromJson(Data,this.getClass()));
    }

    public void Assign(Version source){
        Major = source.Major;
        Mid = source.Mid;
        Minor = source.Minor;
        Build = source.Build;
    }
    public void writeToFile(String fileName) throws IOException{
        File f = new File(fileName);

        FileWriter fw = new FileWriter(f);
        try {
            BufferedWriter bf = new BufferedWriter(fw);

            if (!f.exists())
                f.createNewFile();

            Builder bldr = new Builder();
            Gson gs = bldr.Create();

            String data = new String(gs.toJson(this).getBytes(), UTF_8);
            bf.write(data);
        } finally{
            fw.flush();
            fw.close();
        }




    }

}
