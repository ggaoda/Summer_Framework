package com.gaoda.web;

import com.gaoda.context.AnnotationConfigApplicationContext;
import com.gaoda.context.ApplicationContext;
import com.gaoda.io.PropertyResolver;
import com.gaoda.web.utils.WebUtils;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * ContextLoaderInitializer：负责启动IoC容器，注册Filter与DispatcherServlet。
 *
 * 那么我们的IoC容器，以及注册Servlet、Filter是在哪进行的？
 * 答案是我们在startTomcat()内注册了一个ServletContainerInitializer监听器，
 * 这个监听器负责启动IoC容器与注册Servlet、Filter：
 */
public class ContextLoaderInitializer implements ServletContainerInitializer {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Class<?> configClass;
    final PropertyResolver propertyResolver;

    public ContextLoaderInitializer(Class<?> configClass, PropertyResolver propertyResolver) {
        this.configClass = configClass;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("Servlet container start. ServletContext = {}", ctx);

        String encoding = propertyResolver.getProperty("${summer.web.character-encoding:UTF-8}");
        ctx.setRequestCharacterEncoding(encoding);
        ctx.setResponseCharacterEncoding(encoding);
        // 设置ServletContext:
        WebMvcConfiguration.setServletContext(ctx);
        // 启动IoC容器:
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(this.configClass, this.propertyResolver);
        logger.info("Application context created: {}", applicationContext);

        // register filters:
        WebUtils.registerFilters(ctx);
        // register DispatcherServlet:
        WebUtils.registerDispatcherServlet(ctx, this.propertyResolver);
    }
}