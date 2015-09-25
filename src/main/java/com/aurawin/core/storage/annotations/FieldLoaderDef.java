package com.aurawin.core.storage.annotations;


import com.aurawin.core.storage.entities.Stored;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface FieldLoaderDef {
    Class<? extends Stored> Class() default Stored.class;
    String Target();
    String Query() default "from :Object where Id=:Id";
    String[] Map() default {"Id=Id"};
}
