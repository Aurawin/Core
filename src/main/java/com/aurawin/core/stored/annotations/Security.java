package com.aurawin.core.stored.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Security {
    String DomainIdField() default "";
    String UserNameField() default "";
    String LockCountField() default "";
    String LastLoginField() default "";
    String AuthMethod() default "";
    String LoginMethod() default "";
}
