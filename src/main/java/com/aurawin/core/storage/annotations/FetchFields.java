package com.aurawin.core.storage.annotations;

import com.aurawin.core.storage.entities.Stored;

import javax.persistence.NamedQuery;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface FetchFields {
    FieldLoaderDef[] value();
}
