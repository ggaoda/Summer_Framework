package com.gaoda.web;

import com.gaoda.context.AnnotationConfigApplicationContext;
import com.gaoda.context.ApplicationContext;
import com.gaoda.exception.NestedRuntimeException;
import com.gaoda.io.PropertyResolver;
import com.gaoda.web.utils.WebUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextLoaderListener implements ServletContextListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    // Servlet容器启动时自动调用:
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("init {}.", getClass().getName());
        // 先获取ServletContext引用
        var servletContext = sce.getServletContext();
        // forget
        WebMvcConfiguration.setServletContext(servletContext);

        var propertyResolver = WebUtils.createPropertyResolver();
        String encoding = propertyResolver.getProperty("${summer.web.character-encoding:UTF-8}");
        servletContext.setRequestCharacterEncoding(encoding);
        servletContext.setResponseCharacterEncoding(encoding);
        // 创建IoC容器:
        // 通过getInitParameter("configuration")拿到完整类名，就可以顺利创建IoC容器了
        var applicationContext = createApplicationContext(servletContext.getInitParameter("configuration"), propertyResolver);

        // register filters:
        WebUtils.registerFilters(servletContext);

        // register DispatcherServlet:
        WebUtils.registerDispatcherServlet(servletContext, propertyResolver);

        servletContext.setAttribute("applicationContext", applicationContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (sce.getServletContext().getAttribute("applicationContext") instanceof ApplicationContext applicationContext) {
            applicationContext.close();
        }
    }

    ApplicationContext createApplicationContext(String configClassName, PropertyResolver propertyResolver) {
        logger.info("init ApplicationContext by configuration: {}", configClassName);
        if (configClassName == null || configClassName.isEmpty()) {
            throw new NestedRuntimeException("Cannot init ApplicationContext for missing init param name: configuration");
        }
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new NestedRuntimeException("Could not load class from init param 'configuration': " + configClassName);
        }
        return new AnnotationConfigApplicationContext(configClass, propertyResolver);
    }
}