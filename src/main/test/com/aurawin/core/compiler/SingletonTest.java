package com.aurawin.core.compiler;

import static org.junit.Assert.*;

import com.aurawin.core.VarString;
import com.aurawin.core.compiler.*;
import org.junit.Test;

import java.lang.annotation.Annotation;

public class SingletonTest {
    public static final String defaultResource = "/test/Noid.java";
    @Test
    @SuppressWarnings("unchecked")
    public void testSingletonClass() throws Exception {
        String src=VarString.fromResource(defaultResource);
        Singleton compiler = new Singleton();
        Result r = compiler.compile(src,"Noid");
        if (r.Status== Result.Kind.Success) {
            Object noid = r.Class.getConstructor().newInstance();
            Annotation[] as = noid.getClass().getAnnotations();
        } else {
            for (Diagnostic d:r.Diagnostics)
                System.out.println("Error: "+d.Message);
            throw new Exception("There were errors compiling source.");
        }
    }
    @Test
    public void testSingletonByteCode() throws Exception {
        String src = VarString.fromResource(defaultResource);
        Singleton compiler = new Singleton();
        byte[] ba = compiler.compileByteCode(src,"Noid");
    }
}