package com.aurawin.core.rsr.transport.annotations;

import com.aurawin.core.rsr.def.Version;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Protocol {
    String className() default "";
    Class<? extends Version>  Version();
}
