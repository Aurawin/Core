package com.aurawin.core.compiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Singleton {
    /* Container for a Java compilation unit (ie Java source) in memory. */
    private static class CompilationUnit extends SimpleJavaFileObject {

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
    private static class ByteCode extends SimpleJavaFileObject {

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
    private static class SingleFileManager extends ForwardingJavaFileManager {

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
    private static class SingleClassLoader extends ClassLoader {

        public SingleClassLoader(ByteCode byteCode) {
            byteCode_ = byteCode;
        }

        @Override
        protected Class findClass(String className) throws ClassNotFoundException {
            return defineClass(className, byteCode_.getByteCode(), 0, byteCode_.getByteCode().length);
        }

        ByteCode getFileObject() {
            return byteCode_;
        }

        private final ByteCode byteCode_;
    }
    /* Compiles the provided source code and returns the resulting Class object. */
    private static Class compile(String source, String className) {

        Class clazz = null; // default

        /* Create a list of compilation units (ie Java sources) to compile. */
        List compilationUnits = Arrays.asList(new CompilationUnit(className, source));

        /* The diagnostic listener gives you a way of examining the source when
         * the compile fails. You don't need it, but it makes debugging easier. */
        DiagnosticCollector diagnosticListener = new DiagnosticCollector();

        /* Get a Java compiler to use. (If this returns null there is a good
         * chance you're using a JRE instead of a JDK.) */
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        /* Set up the target file manager and call the compiler. */
        SingleFileManager singleFileManager = new SingleFileManager(compiler, new ByteCode(className));
        JavaCompiler.CompilationTask compile = compiler.getTask(null, singleFileManager, diagnosticListener, null,
                null, compilationUnits);

        if (!compile.call()) {
            /* Compilation failed: Output the compiler errors to stderr. */
            List<Diagnostic> Diags = diagnosticListener.getDiagnostics();
            for (Diagnostic diagnostic : Diags) {
                System.err.println(diagnostic);
            }
        } else {
            /* Compilation succeeded: Get the Class object. */
            try {
                clazz = singleFileManager.getClassLoader().findClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return clazz;
    }
}
