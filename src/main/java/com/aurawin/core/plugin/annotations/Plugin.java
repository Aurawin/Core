package com.aurawin.core.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Plugin {
    String Name() default "";
    String Namespace() default "";
    String Transport() default "HTTP/1.1";
    String Title() default "";
    String Prompt() default "";
    String Description() default "";
    String Vendor() default "";
    String ClassName() default "";
    int Version() default 0;
}