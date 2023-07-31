    package com.itranswarp.summer.aop.before;

    import com.gaoda.annotation.Component;
    import com.gaoda.aop.BeforeInvocationHandlerAdapter;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.lang.reflect.Method;

    @Component
public class LogInvocationHandler extends BeforeInvocationHandlerAdapter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void before(Object proxy, Method method, Object[] args) {
        logger.info("[Before] {}()", method.getName());
    }
}