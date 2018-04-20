package com.aurawin.core.plugin.annotations;


import com.aurawin.core.plugin.FormatIO;

import java.lang.annotation.*;
import java.util.ArrayList;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    boolean Anonymous() default false;
    boolean Enabled() default true;
    boolean RequiresTransformation() default false;
    String Name();
    String[] Roles() default {};
    String Namespace();
    String Title() default "";
    String Prompt() default "";
    String Description() default "";
    String Method() default "";
    FormatIO Format() default FormatIO.None;
}