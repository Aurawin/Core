package com.aurawin.core.compiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import com.aurawin.core.compiler.Result;

public class Singleton {
    /* Container for a Java compilation unit (ie Java source) in memory. */
    private class CompilationUnit extends SimpleJavaFileObject {

        public CompilationUnit(String className, String source) {
            super(URI.create("file:///" + className + ".java"), Kind.SOURCE);
            source_ = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source_;
        }

        @Override
        public OutputStream openOutputStream() {
            throw new IllegalStateException();
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(source_.getBytes());
        }

        private final String source_;
    }

    /* Container for Java byte code in memory. */
    private class ByteCode extends SimpleJavaFileObject {

        public ByteCode(String className) {
            super(URI.create("byte:///" + className + ".class"), Kind.CLASS);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return null;
        }

        @Override
        public OutputStream openOutputStream() {
            byteArrayOutputStream_ = new ByteArrayOutputStream();
            return byteArrayOutputStream_;
        }

        @Override
        public InputStream openInputStream() {
            return null;
        }

        public byte[] getByteCode() {
            return byteArrayOutputStream_.toByteArray();
        }

        private ByteArrayOutputStream byteArrayOutputStream_;
    }
    /* A file manager for a single class. */
    public class SingleFileManager extends ForwardingJavaFileManager {

        public SingleFileManager(JavaCompiler compiler, ByteCode byteCode) {
            super(compiler.getStandardFileManager(null, null, null));
            singleClassLoader_ = new SingleClassLoader(byteCode);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location notUsed, String className, JavaFileObject.Kind kind,
                                                   FileObject sibling) throws IOException {
            return singleClassLoader_.getFileObject();
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return singleClassLoader_;
        }

        public SingleClassLoader getClassLoader() {
            return singleClassLoader_;
        }

        private final SingleClassLoader singleClassLoader_;
    }

    /* A class loader for a single class. */
    public class SingleClassLoader extends ClassLoader {

        public SingleClassLoader(ByteCode byteCode) {
            byteCode_ = byteCode;
        }

        @Override
        public Class findClass(String className) throws ClassNotFoundException {
            return defineClass(className, byteCode_.getByteCode(), 0, byteCode_.getByteCode().length);
        }

        ByteCode getFileObject() {
            return byteCode_;
        }

        private final ByteCode byteCode_;
    }
    /* Compiles the provided source code and returns the resulting Class object. */
    public  Result compile(String source, String className) {
        Result r = new Result();
        r.Status=Result.Kind.Failure;
        List compilationUnits = Arrays.asList(new CompilationUnit(className, source));
        DiagnosticCollector diagnosticListener = new DiagnosticCollector();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        SingleFileManager singleFileManager = new SingleFileManager(compiler, new ByteCode(className));
        JavaCompiler.CompilationTask compile = compiler.getTask(null, singleFileManager, diagnosticListener, null, null, compilationUnits);

        if (compile.call()) {
            r.processDiagnostics(diagnosticListener.getDiagnostics());
            r.Status=Result.Kind.Success;
            r.Code=singleFileManager.singleClassLoader_.byteCode_.getByteCode();
            try {
                r.Class = singleFileManager.getClassLoader().findClass("com.aurawin.core.stored.entities."+className);
            } catch (ClassNotFoundException e) {
                r.Status=Result.Kind.Exception;
            }
        } else {
            r.processDiagnostics(diagnosticListener.getDiagnostics());
        }
        return r;
    }

    public byte[] compileByteCode(String source, String className){
        List compilationUnits = Arrays.asList(new CompilationUnit(className, source));
        DiagnosticCollector diagnosticListener = new DiagnosticCollector();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        SingleFileManager singleFileManager = new SingleFileManager(compiler, new ByteCode(className));
        JavaCompiler.CompilationTask compile = compiler.getTask(null, singleFileManager, diagnosticListener, null,null, compilationUnits);
        if (compile.call()) {
            return singleFileManager.singleClassLoader_.byteCode_.getByteCode();
        }
        return null;
    }
}
