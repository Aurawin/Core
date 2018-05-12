package com.aurawin.core.stored.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface EntityDispatch {
    boolean onPurge() default false;
    boolean onCreated()  default false;
    boolean onDeleted()  default false;
    boolean onUpdated()  default false;

}
