package com.itranswarp.summer.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Polite {

}