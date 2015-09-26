package com.aurawin.core.storage.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface QueryByName {
    String Name() default "";
    String [] Fields() default "";
}
