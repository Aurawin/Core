package com.aurawin.core.stored.annotations;


import com.aurawin.core.stored.entities.Stored;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface FetchField {
    Class<? extends Stored> Class() default Stored.class;
    String Target();
}
