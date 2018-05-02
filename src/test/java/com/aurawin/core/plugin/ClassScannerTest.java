package com.aurawin.core.plugin;

import com.aurawin.core.ClassScanner;
import com.aurawin.core.plugin.annotations.Plugin;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassScannerTest {
    ClassScanner cs ;

  @Before
    public void before(){
      cs = new ClassScanner();


  }
  @Test
    public void test() throws Exception {

      Set<Class<? extends Plug>> ca = cs.scanPackageForPlugins(com.aurawin.core.Package.class);
        for (Class c : ca){
            Annotation ed = c.getAnnotation(Plugin.class);
            if (ed != null) {
                System.out.println("Plugin Found "+ c.getName());
            }
      }

  }
}