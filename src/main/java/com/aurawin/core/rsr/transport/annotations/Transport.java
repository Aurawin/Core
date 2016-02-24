package com.aurawin.core.rsr.transport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Transport {
    String Name() default "";
    String Protocol() default "";
    String[] Fields() default {};

}
