package com.aurawin.core.storage.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface FetchFields {
    FetchField[] value();
}
