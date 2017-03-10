package com.aurawin.core.stored.annotations;

import org.hibernate.query.Query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface QueryById {
    String Name() default "";
    String [] Fields() default "";

}
