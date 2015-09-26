package com.aurawin.core.storage.annotations;


import com.aurawin.core.storage.entities.Stored;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface FetchField {
    Class<? extends Stored> Class() default Stored.class;
    String Target();
}
