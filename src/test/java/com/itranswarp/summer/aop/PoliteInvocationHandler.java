package com.itranswarp.summer.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 我们要实现的AOP功能是增强带@Polite注解的方法，把返回值Hello, Bob.改为Hello, Bob!，让欢迎气氛更强烈一点，因此，编写一个
 */
public class PoliteInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object bean, Method method, Object[] args) throws Throwable {
        // 修改标记了@Polite的方法返回值:
        if (method.getAnnotation(Polite.class) != null) {
            String ret = (String) method.invoke(bean, args);
            if (ret.endsWith(".")) {
                ret = ret.substring(0, ret.length() - 1) + "!";
            }
            return ret;
        }
        return method.invoke(bean, args);
    }
}