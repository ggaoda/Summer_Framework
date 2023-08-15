package com.gaoda.annotation;

import java.lang.annotation.*;


// 定义@Transactional，这里就不允许单独在方法处定义，直接在class级别启动所有public方法的事务：
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Transactional {

    // 默认值platformTransactionManager表示用名字为platformTransactionManager的Bean来管理事务。
    String value() default "platformTransactionManager";
}