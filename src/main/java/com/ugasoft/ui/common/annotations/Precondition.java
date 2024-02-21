package com.ugasoft.ui.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Precondition {
    String className() default "";

    String page() default "";

    String location() default "";

    String user() default "";

    String login() default "";

    String password() default "";

    String email() default "";

    String flag() default "";

    boolean flagValue() default true;
}