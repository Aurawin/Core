package com.aurawin.core.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Result {
    public enum Kind {Exception,Failure,Success}
    public Kind Status;
    public ArrayList<Diagnostic> Diagnostics;
    public Class Class;
    public byte[] Code;
    public Result(){
        Status = Kind.Failure;
        Diagnostics=new ArrayList<Diagnostic>();
        Class=null;
        Code=null;
    }
    public void processDiagnostics(List<javax.tools.Diagnostic> Diags){
        Diagnostics.clear();
        for (javax.tools.Diagnostic d : Diags) {
            Diagnostics.add(new Diagnostic(d.getLineNumber(),d.getColumnNumber(),d.getMessage(null)));
        }
    }
}
