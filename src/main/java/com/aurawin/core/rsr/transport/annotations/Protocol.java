package com.aurawin.core.rsr.transport.annotations;

import com.aurawin.core.rsr.def.Version;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Protocol {
    Class<? extends Version>  Version();
}
