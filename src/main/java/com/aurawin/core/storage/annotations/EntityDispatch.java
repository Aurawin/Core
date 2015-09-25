package com.aurawin.core.storage.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface EntityDispatch {
    boolean onCreated()  default false;
    boolean onDeleted()  default false;
    boolean onUpdated()  default false;

}
