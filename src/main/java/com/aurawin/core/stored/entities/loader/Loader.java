package com.aurawin.core.stored.entities.loader;

import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.Module;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.tools.SimpleJavaFileObject;

public class Loader {

    public boolean Injected;

    public Loader() {
        Injected=false;
        Cache = new ArrayList<ModuleLoader>();
        DefaultClassLoader=Thread.currentThread().getContextClassLoader();
    }
    public final ClassLoader DefaultClassLoader;
    public final List<ModuleLoader> Cache;

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

        public void Release(){
            byteArrayOutputStream_.reset();
            byteArrayOutputStream_=null;
        }
    }
    public class ModuleLoader extends ClassLoader {
        public ModuleLoader(Module module, ByteCode bc) {
            module_ = module;
            byteCode_= bc;
            class_=null;
            Cache.add(0,this);
        }

        @Override
        public Class findClass(String nameSpace) throws ClassNotFoundException {
            Class c = null;
            if (module_!=null) {
                if (module_.getNamespace().compareToIgnoreCase(nameSpace) == 0) {
                    if (class_==null)
                        class_ = defineClass(module_.getPackage() + "." + module_.getName(), byteCode_.getByteCode(), 0, byteCode_.getByteCode().length);
                    c = class_;
                } else {
                    ModuleLoader ml = Cache.stream()
                            .filter(e -> e.module_.getNamespace().equalsIgnoreCase(nameSpace))
                            .findFirst()
                            .orElse(null);
                    if (ml != null) {
                        c = ml.findClass(nameSpace);
                    }
                }
            }
            if (c==null)
                c = DefaultClassLoader.loadClass(nameSpace);
            return c;
        }

        ByteCode getFileObject() {
            return byteCode_;
        }
        private Class<?> class_;
        private ByteCode byteCode_;
        private Module module_;

        public void Release(){
            Cache.remove(this);
            class_=null;

            byteCode_.Release();
            byteCode_=null;

            module_.Empty();
            module_=null;
        }
        public boolean comparesTo(ModuleLoader ml){

            return ( (module_!=null) && (ml!=null) && ml.module_!=null) && (module_.getId()==ml.module_.getId());
        }
    }
    public Result Check(Module m){
        Result r = new Result();
        try {
            r.Module=m;
            r.State=Result.Kind.Found;
            if (r.Module.Loader==null){
                r.Module.Loader=Cache.stream()
                        .filter(e -> e.module_.getNamespace().equalsIgnoreCase(m.getNamespace()))
                        .findFirst()
                        .orElse(null);

            }
            if (m.Loader==null){
                ByteCode bc = new ByteCode(r.Module.getName());
                m.Loader=new ModuleLoader(m,bc);
                bc.setByteCode(r.Module.getCode());
                Injected=true;
            }
            try {
                r.Class = m.Loader.findClass(r.Module.getNamespace());
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
    public void Uninstall(String namespace){
        Iterator<ModuleLoader> it = Cache.stream()
                .filter(c -> c.module_.getNamespace().equalsIgnoreCase(namespace))
                .iterator();
        ModuleLoader mlInstall=Cache.stream()
                .filter(c -> c.module_.getNamespace().equalsIgnoreCase(namespace))
                .findFirst()
                .orElse(null);
        while (it.hasNext()){
            ModuleLoader ml = it.next();
            Cache.remove(ml);
            Entities.removeAnnotatedClass(ml.class_);
            ml.Release();
        }
    }
    public Stored New(String namespace){
        ModuleLoader ml = Cache.stream()
                .filter(e -> e.module_.getNamespace().equalsIgnoreCase(namespace))
                .findFirst()
                .orElse(null);

        if (ml!=null) {
            try {
                return (Stored) ml.class_.getConstructor().newInstance();
            } catch (Exception e){
                return null;
            }
        } else {
            return null;
        }

    }
    public Result Check(String namespace){
        Result r = new Result();
        try {
            r.Module = (Module) Entities.Lookup(Module.class, namespace);
            if (r.Module!=null){
                r.State=Result.Kind.Found;

                Uninstall(namespace);

                if (r.Module.Loader==null){
                    r.Module.Loader=Cache.stream()
                            .filter(e -> e.module_.getNamespace().equalsIgnoreCase(namespace))
                            .findFirst()
                            .orElse(null);

                }
                if (r.Module.Loader==null){

                    ByteCode bc = new ByteCode(r.Module.getName());
                    r.Module.Loader=new ModuleLoader(r.Module,bc);
                    bc.setByteCode(r.Module.getCode());
                    Injected=true;

                }
                try {
                    r.Class = r.Module.Loader.findClass(r.Module.getNamespace());
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
