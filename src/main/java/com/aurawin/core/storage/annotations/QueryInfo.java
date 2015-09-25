package com.aurawin.core.storage.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface QueryInfo {
    String Name() default "";
    String Value() default "";
    String [] Fields() default "";
}
