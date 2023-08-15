package com.gaoda.context;

import com.gaoda.exception.BeanCreationException;
import jakarta.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 先定义BeanDefinition，它能从Annotation中提取到足够的信息，便于后续创建Bean、设置依赖、调用初始化方法等：
 */
public class BeanDefinition implements Comparable<BeanDefinition> {

    // 全局唯一的Bean Name:
    private final String name;
    // Bean的声明类型:
    private final Class<?> beanClass;
    // Bean的实例:
    private Object instance = null;
    // 构造方法/null:
    private final Constructor<?> constructor;
    // 工厂方法名称/null:
    private final String factoryName;
    // 工厂方法/null:
    private final Method factoryMethod;
    // Bean的顺序:
    private final int order;
    // 是否标识@Primary:
    private final boolean primary;

    // init destroy方法名称
    private String initMethodName;
    private String destroyMethodName;

    // init destroy方法
    private Method initMethod;
    private Method destroyMethod;


    /**
     * 构造方法一 :
     * 对于自己定义的带@Component注解的Bean，我们需要获取Class类型，获取构造方法来创建Bean，
     * 然后收集@PostConstruct和@PreDestroy标注的初始化与销毁的方法，以及其他信息，
     * 如@Order定义Bean的内部排序顺序，@Primary定义存在多个相同类型时返回哪个“主要”Bean。
     * @param name
     * @param beanClass
     * @param constructor
     * @param order
     * @param primary
     * @param initMethodName
     * @param destroyMethodName
     * @param initMethod
     * @param destroyMethod
     */
    public BeanDefinition(String name, Class<?> beanClass, Constructor<?> constructor, int order, boolean primary, String initMethodName,
            String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.factoryName = null;
        this.factoryMethod = null;
        this.order = order;
        this.primary = primary;
        // 允许使用反射构造方法创建
        constructor.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
    }

    /**
     * 构造方法二
     * 对于@Configuration定义的@Bean方法，我们把它看作Bean的工厂方法，
     * 我们需要获取方法返回值作为Class类型，方法本身作为创建Bean的factoryMethod，
     * 然后收集@Bean定义的initMethod和destroyMethod标识的初始化于销毁的方法名，以及其他@Order、@Primary等信息。
     * @param name
     * @param beanClass
     * @param factoryName
     * @param factoryMethod
     * @param order
     * @param primary
     * @param initMethodName
     * @param destroyMethodName
     * @param initMethod
     * @param destroyMethod
     */
    public BeanDefinition(String name, Class<?> beanClass, String factoryName, Method factoryMethod, int order, boolean primary, String initMethodName,
            String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = null;
        this.factoryName = factoryName;
        this.factoryMethod = factoryMethod;
        this.order = order;
        this.primary = primary;
        // 允许反射
        factoryMethod.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
    }


    /**
     * set设置初始方法和销毁方法
     * @param initMethodName
     * @param destroyMethodName
     * @param initMethod
     * @param destroyMethod
     */
    private void setInitAndDestroyMethod(String initMethodName, String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;

        // 允许反射

        if (initMethod != null) {
            initMethod.setAccessible(true);
        }
        if (destroyMethod != null) {
            destroyMethod.setAccessible(true);
        }
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    // region get方法

    @Nullable
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Nullable
    public String getFactoryName() {
        return this.factoryName;
    }

    @Nullable
    public Method getFactoryMethod() {
        return this.factoryMethod;
    }

    @Nullable
    public Method getInitMethod() {
        return this.initMethod;
    }

    @Nullable
    public Method getDestroyMethod() {
        return this.destroyMethod;
    }

    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Nullable
    public Object getInstance() {
        return this.instance;
    }
    // 获取必需实例
    public Object getRequiredInstance() {
        if (this.instance == null) {
            throw new BeanCreationException(String.format("Instance of bean with name '%s' and type '%s' is not instantiated during current stage.",
                    this.getName(), this.getBeanClass().getName()));
        }
        return this.instance;
    }

    public void setInstance(Object instance) {
        Objects.requireNonNull(instance, "Bean instance is null.");
        if (!this.beanClass.isAssignableFrom(instance.getClass())) {
            throw new BeanCreationException(String.format("Instance '%s' of Bean '%s' is not the expected type: %s", instance, instance.getClass().getName(),
                    this.beanClass.getName()));
        }
        this.instance = instance;
    }
    // 是否primary
    public boolean isPrimary() {
        return this.primary;
    }

    /**
     * 仔细编写BeanDefinition的toString()方法，使之能打印出详细的信息。
     * @return
     */
    @Override
    public String toString() {
        return "BeanDefinition [name=" + name + ", beanClass=" + beanClass.getName() + ", factory=" + getCreateDetail() + ", init-method="
                + (initMethod == null ? "null" : initMethod.getName()) + ", destroy-method=" + (destroyMethod == null ? "null" : destroyMethod.getName())
                + ", primary=" + primary + ", instance=" + instance + "]";
    }

    /**
     * 获取创建详情
     * @return
     */
    String getCreateDetail() {
        if (this.factoryMethod != null) {
            String params = String.join(", ", Arrays.stream(this.factoryMethod.getParameterTypes()).map(t -> t.getSimpleName()).toArray(String[]::new));
            return this.factoryMethod.getDeclaringClass().getSimpleName() + "." + this.factoryMethod.getName() + "(" + params + ")";
        }
        return null;
    }

    @Override
    public int compareTo(BeanDefinition def) {
        int cmp = Integer.compare(this.order, def.order);
        if (cmp != 0) {
            return cmp;
        }
        return this.name.compareTo(def.name);
    }
}