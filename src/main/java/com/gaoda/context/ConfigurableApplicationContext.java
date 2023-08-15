package com.gaoda.context;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * 给Framework级别的代码用的ConfigurableApplicationContext接口：
 * Used for BeanPostProcessor.
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(String name);

    @Nullable
    BeanDefinition findBeanDefinition(String name, Class<?> requiredType);

    Object createBeanAsEarlySingleton(BeanDefinition def);
}