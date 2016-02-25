package com.aurawin.core.plugin.annotations;


import com.aurawin.core.plugin.FormatIO;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    String Name() default "";
    String Namespace() default "";
    String Title() default "";
    String Prompt() default "";
    String Description() default "";
    FormatIO Format() default FormatIO.None;
    String [] Fields() default {};
}