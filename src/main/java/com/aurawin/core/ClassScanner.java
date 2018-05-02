package com.aurawin.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.URL;
import com.aurawin.core.Package;
import com.aurawin.core.plugin.Plug;
import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.solution.Namespace;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.Namespaced;

import com.aurawin.core.stored.entities.UniqueId;
import com.google.common.reflect.ClassPath;
import org.reflections.Reflections;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassScanner {

    public Set<Class<? extends Plug>> scanPackageForPlugins(Class<? extends Package>  basePackage) throws Exception{
        Reflections reflections = new Reflections(basePackage.getPackage().getName());
        return reflections.getSubTypesOf(Plug.class);


    }

    public Set<Class<?> > scanPackageForNamespaced(Class<? extends Package>  basePackage) throws Exception{
        Reflections reflections = new Reflections(basePackage.getPackage().getName());
        return reflections.getTypesAnnotatedWith(Namespaced.class);


    }

    public ArrayList<UniqueId> scanPackageForUniqueIdentity(Class<? extends Package> basePackage) throws Exception{
        Reflections reflections = new Reflections(basePackage.getPackage().getName());
        Set<Class<?> > set = reflections.getTypesAnnotatedWith(Namespaced.class);
        ArrayList<UniqueId> r = new ArrayList<>();
        UniqueId id;
        for (Class c:set){
            id = Namespace.Entities.getUniqueId(c);
            r.add(id);
        }
        return r;
    }


}
