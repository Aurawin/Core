package com.aurawin.core.compiler;


public class Diagnostic {
    public long Line;
    public long Column;
    public String Message;

    public Diagnostic(long line, long col, String message) {
        Line = line;
        Column = col;
        Message = message;
    }
}
