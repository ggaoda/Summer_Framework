package com.gaoda.context;

public interface BeanPostProcessor {

    /**
     * Invoked after new Bean().
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Invoked after bean.init() called.
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Invoked before bean.setXyz() called.
     * 注入依赖时,应该使用的Bean实例:
     */
    default Object postProcessOnSetProperty(Object bean, String beanName) {
        return bean;
    }
}