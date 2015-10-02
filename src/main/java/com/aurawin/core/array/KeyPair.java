package com.aurawin.core.array;

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
            Append(VarString.Extract(sItem, DelimiterField, VarString.ExtractOption.eoSingleton));
        }
    }
    public KeyPair(KeyPair value){
        Assign(value);
    }

    public KeyPair(){

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
            Append(VarString.Extract(sItem, DelimiterField, VarString.ExtractOption.eoSingleton));
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
            saItem=VarString.Extract(Bytes.toString(bItem),DelimiterField, VarString.ExtractOption.eoSingleton);
            Append(saItem);
        }
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

    public void Release(){
        clear();
    }

}
