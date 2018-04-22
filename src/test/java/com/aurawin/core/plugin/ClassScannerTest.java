package com.aurawin.core.plugin;

import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.stored.annotations.EntityDispatch;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.*;

public class ClassScannerTest {
    private static final String packageName = "com.aurawin";

    ClassScanner cs ;

  @Before
    public void before(){
      cs = new ClassScanner();


  }
  @Test
    public void test() throws Exception {

        Class[] ca = cs.scanPackage(packageName);
        for (Class c : ca){
            Annotation ed = c.getAnnotation(Plugin.class);
            if (ed != null) {
                System.out.println("Plugin Found "+ c.getName());
            }
      }
  }
}