package com.aurawin.core.plugin.annotations;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.transport.methods.MethodProcess;
import com.aurawin.core.rsr.transport.methods.http.GET;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Plugin {
    boolean Anonymous() default false;
    String Name();
    String Package();
    String Namespace();
    String Title() default "";
    String Prompt() default "";
    String Description() default "";
    String Vendor() default "";
    String[] Roles() default {};
    Class<? extends Item> Transport() default Item.class;
    Class<? extends MethodProcess> Method() default GET.class;
}
