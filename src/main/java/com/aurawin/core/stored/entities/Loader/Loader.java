package com.aurawin.core.stored.entities.loader;

import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.Module;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class Loader {
    public class ByteCode extends SimpleJavaFileObject {

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
        public void setByteCode(byte[] code) throws IOException{
            if (byteArrayOutputStream_==null) {
                openOutputStream();
            } else {
                byteArrayOutputStream_.reset();
            }
            byteArrayOutputStream_.write(code);
        }

        private ByteArrayOutputStream byteArrayOutputStream_;
    }
    public class ModuleLoader extends ClassLoader {

        public ModuleLoader(ByteCode byteCode) {
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
    public Result Check(Module m){
        Result r = new Result();
        try {
            r.Module=m;
            r.State=Result.Kind.Found;
            ByteCode bc = new ByteCode(r.Module.getName());
            try {
                bc.setByteCode(r.Module.getCode());
                ModuleLoader l = new ModuleLoader(bc);
                r.Class = l.findClass(r.Module.getName());
            } catch (Exception e){
                r.State= Result.Kind.Exception;
                r.Class=null;
            }
        } catch (Exception e){
            r.State=Result.Kind.Exception;
            r.Class=null;
        }
        return r;
    }
    public Result Check(Entities entities,String namespace){
        Result r = new Result();
        try {
            r.Module = (Module) Entities.Lookup(Module.class, entities, namespace);
            if (r.Module!=null){
                r.State=Result.Kind.Found;
                ByteCode bc = new ByteCode(r.Module.getName());
                try {
                    bc.setByteCode(r.Module.getCode());
                    ModuleLoader l = new ModuleLoader(bc);
                    r.Class = l.findClass(r.Module.getName());
                } catch (Exception e){
                    r.State= Result.Kind.Exception;
                    r.Class=null;
                }
            }
        } catch (Exception e){
            r.State=Result.Kind.Exception;
            r.Class=null;
        }
        return r;
    }


}
