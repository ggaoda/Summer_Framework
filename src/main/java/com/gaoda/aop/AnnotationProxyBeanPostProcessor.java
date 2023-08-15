package com.gaoda.aop;

import com.gaoda.context.ApplicationContextUtils;
import com.gaoda.context.BeanDefinition;
import com.gaoda.context.BeanPostProcessor;
import com.gaoda.context.ConfigurableApplicationContext;
import com.gaoda.exception.AopConfigException;
import com.gaoda.exception.BeansException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 范型ProxyBeanPostProcessor,方便针对不同模块扩展
 * @param <A> 对应AOP注解如Around After .....
 */
public abstract class AnnotationProxyBeanPostProcessor<A extends Annotation> implements BeanPostProcessor {

    // 保存了原始Bean的引用，因为IoC容器在后续的注入阶段要把相关依赖和值注入到原始Bean
    Map<String, Object> originBeans = new HashMap<>();
    Class<A> annotationClass;

    public AnnotationProxyBeanPostProcessor() {
        this.annotationClass = getParameterizedType();
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // 检测类是否有对应注解 Around | Before | After
        A anno = beanClass.getAnnotation(annotationClass);
        if (anno != null) { // 有注解
            String handlerName;
            try {
                // 根据注解中配置的值去查找对应Bean作为InvocationHandler
                handlerName = (String) anno.annotationType().getMethod("value").invoke(anno);
            } catch (ReflectiveOperationException e) {
                throw new AopConfigException(String.format("@%s must have value() returned String type.", this.annotationClass.getSimpleName()), e);
            }                       //    代理类  代理对象  handler增强方法
            Object proxy = createProxy(beanClass, bean, handlerName); // 创建Proxy
            originBeans.put(beanName, bean); // 保存原始Bean
            return proxy;  // 返回代理
        } else {
            return bean; // 没有对应注解直接返回原始Bean
        }
    }

    Object createProxy(Class<?> beanClass, Object bean, String handlerName) {
        //
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) ApplicationContextUtils.getRequiredApplicationContext();

        BeanDefinition def = ctx.findBeanDefinition(handlerName); // 找到handler的BeanDefinition
        if (def == null) {//没找到抛出异常
            throw new AopConfigException(String.format("@%s proxy handler '%s' not found.", this.annotationClass.getSimpleName(), handlerName));
        }
        Object handlerBean = def.getInstance(); // 尝试获取Bean对象
        if (handlerBean == null) {  // Bean对象为null表示还没创建过
            handlerBean = ctx.createBeanAsEarlySingleton(def); // 创建
        }
        if (handlerBean instanceof InvocationHandler handler) { // 如果handler是InvocationHandler的子类
            return ProxyResolver.getInstance().createProxy(bean, handler); // 则通过ProxyResolver获取实例来创建代理
        } else { // 否则抛异常
            throw new AopConfigException(String.format("@%s proxy handler '%s' is not type of %s.", this.annotationClass.getSimpleName(), handlerName,
                    InvocationHandler.class.getName()));
        }
    }


    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object origin = this.originBeans.get(beanName);
        return origin != null ? origin : bean;
    }

    @SuppressWarnings("unchecked")
    private Class<A> getParameterizedType() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type.");
        }
        ParameterizedType pt = (ParameterizedType) type;
        Type[] types = pt.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " has more than 1 parameterized types.");
        }
        Type r = types[0];
        if (!(r instanceof Class<?>)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type of class.");
        }
        return (Class<A>) r;
    }
}