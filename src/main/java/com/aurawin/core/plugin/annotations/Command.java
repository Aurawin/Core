package com.aurawin.core.plugin.annotations;


import com.aurawin.core.plugin.FormatIO;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    boolean Anonymous() default false;
    boolean Enabled() default true;
    String Name();
    String Namespace();
    String Title() default "";
    String Prompt() default "";
    String Description() default "";
    FormatIO Format() default FormatIO.None;
}