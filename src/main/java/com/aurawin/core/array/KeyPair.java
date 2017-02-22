package com.aurawin.core.array;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyPair extends ArrayList<KeyItem> {
    public String DelimiterField = "\\=";
    public String DelimiterItem = "\r\n";

    public KeyItem Append(String[] itm){
        KeyItem ki = null;
        switch (itm.length){
            case 0:
                ki=new KeyItem();
                break;
            case 1:
                ki=new KeyItem(itm[0]);
                break;
            case 2:
                ki=new KeyItem(itm[0],itm[1]);
                break;
            default:
                ki = new KeyItem();
                break;

        }
        if (ki!=null) add(ki);
        return ki;
    }

    public KeyPair(String args){
        String[] saItems = args.split(DelimiterItem);
        for (String sItem : saItems) {
            Append(VarString.Extract(sItem, DelimiterField, VarString.ExtractOption.Singleton));
        }
    }
    public KeyPair(KeyPair value){
        Assign(value);
    }

    public KeyPair(){

    }
    public void Empty(){
        clear();
    }
    public KeyItem Update(String Name, Object Value){
        for (KeyItem itm : this){
            if (itm.Name.compareTo(Name)==0) {
                itm.Data=Value;
                itm.Stale=false;
                return itm;
            }
        }
        KeyItem itm = new KeyItem(Name,Value);
        itm.Data=Value;
        this.add(itm);
        return itm;
    }
    public KeyItem Update(String Name, long id){
        for (KeyItem itm : this){
            if (itm.Name.compareTo(Name)==0) {
                itm.Id=id;
                itm.Stale=false;
                return itm;
            }
        }
        KeyItem itm = new KeyItem(Name,id);
        this.add(itm);
        return itm;
    }
    public KeyItem Find(String Name){
        return this.stream()
                .filter(i -> i.Name.equalsIgnoreCase(Name))
                .findFirst()
                .orElse(null);
    }
    public KeyItem Update(String Name, String Value){
        for (KeyItem itm : this){
            if (itm.Name.compareTo(Name)==0) {
                itm.Value=Value;
                itm.Stale=false;
                return itm;
            }
        }
        KeyItem itm = new KeyItem(Name,Value);
        this.add(itm);
        return itm;
    }
    public KeyItem setStreams(String Name, boolean streams){
        for (KeyItem itm : this){
            if (itm.Name.compareTo(Name)==0) {
                itm.Streams=streams;
                return itm;
            }
        }
        KeyItem itm = new KeyItem(Name,"");
        this.add(itm);
        itm.Streams=streams;
        return null;
    }
    public void Assign(KeyPair value){
        DelimiterField=value.DelimiterField;
        DelimiterItem=value.DelimiterItem;
        clear();
        for (KeyItem ki : value){
            add(new KeyItem(ki));
        }
    }
    public void Load(String Data){
        String[] saItems = Data.split(DelimiterItem);
        for (String sItem : saItems) {
            Append(VarString.Extract(sItem, DelimiterField, VarString.ExtractOption.Singleton));
        }
    }
    public void Invalidate(){
        for (KeyItem itm:this) itm.Stale=true;
    }
    public void Purge(){
        Iterator <KeyItem> it = iterator();
        while (it.hasNext()){
            KeyItem itm=it.next();
            if (itm.Stale==true) remove(itm);
        }
    }
    public void Load(byte[] Data){
        byte[] bItem;
        int iChunk;
        clear();
        List<SearchResult> srItems;
        String[] saItem;
        srItems = Bytes.Split(Data,DelimiterItem.getBytes());
        for( SearchResult r : srItems) {
            iChunk=r.End-r.Start;
            bItem=new byte[iChunk];
            System.arraycopy(Data,r.Start,bItem,0,iChunk);
            saItem=VarString.Extract(Bytes.toString(bItem),DelimiterField, VarString.ExtractOption.Singleton);
            Append(saItem);
        }
    }
    public String Stream(){
        KeyItem itm = null;
        StringBuilder sr = new StringBuilder();

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext(); ){
            itm=itr.next();
            if (itm.Streams==true) {
                sr.append(itm.Name+DelimiterField+itm.Value+DelimiterItem);
            }
        }
        int len = sr.length();
        if (len>0) sr.setLength(len-DelimiterItem.length());
        return sr.toString();
    }
    public String ValueAsString(String Name){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext(); ){
           itm=itr.next();
           if (itm.Name.compareToIgnoreCase(Name)==0){
               return itm.Value;
           }
        }
        return "";
    }
    public int ValueAsInteger(String Name){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return Integer.parseInt(itm.Value);
            }
        }
        return 0;
    }
    public float ValueAsFloat(String Name){
        KeyItem itm;
        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return Float.parseFloat(itm.Value);
            }
        }
        return 0;
    }
    public long ValueAsLong(String Name, long Default){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return VarString.toLong(itm.Value,0);
            }
        }
        return Default;
    }
    public Object getData(String Name){
        KeyItem itm;
        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return itm.Data;
            }
        }
        return null;
    }
    public void Release(){
        clear();
    }

}
