package com.gaoda.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {

    /**
     * URL mapping.
     */
    String value();

}