package com.gaoda.web.utils;

import com.gaoda.context.ApplicationContextUtils;
import com.gaoda.io.PropertyResolver;
import com.gaoda.utils.ClassPathUtils;
import com.gaoda.utils.YamlUtils;
import com.gaoda.web.DispatcherServlet;
import com.gaoda.web.FilterRegistrationBean;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.*;

public class WebUtils {
    public static final String DEFAULT_PARAM_VALUE = "\0\t\0\t\0";

    static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    static final String CONFIG_APP_YAML = "/application.yml";
    static final String CONFIG_APP_PROP = "/application.properties";

    public static void registerDispatcherServlet(ServletContext servletContext, PropertyResolver propertyResolver) {
        // 实例化DispatcherServlet:
        var dispatcherServlet = new DispatcherServlet(ApplicationContextUtils.getRequiredApplicationContext(), propertyResolver);
        logger.info("register servlet {} for URL '/'", dispatcherServlet.getClass().getName());
        // 注册DispatcherServlet:
        var dispatcherReg = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcherReg.addMapping("/");
        dispatcherReg.setLoadOnStartup(0);
    }


    public static void registerFilters(ServletContext servletContext) {
        var applicationContext = ApplicationContextUtils.getRequiredApplicationContext();
        for (var filterRegBean : applicationContext.getBeans(FilterRegistrationBean.class)) {
            List<String> urlPatterns = filterRegBean.getUrlPatterns();
            if (urlPatterns == null || urlPatterns.isEmpty()) {
                throw new IllegalArgumentException("No url patterns for {}" + filterRegBean.getClass().getName());
            }
            var filter = Objects.requireNonNull(filterRegBean.getFilter(), "FilterRegistrationBean.getFilter() must not return null.");
            logger.info("register filter '{}' {} for URLs: {}", filterRegBean.getName(), filter.getClass().getName(), String.join(", ", urlPatterns));
            var filterReg = servletContext.addFilter(filterRegBean.getName(), filter);
            filterReg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, urlPatterns.toArray(String[]::new));
        }
    }


    /**
     * 创建IoC容器时，需要的配置文件从哪读？
     * 这里我们采用Spring Boot的方式，默认从classpath的application.yml或application.properties读。
     * Try load property resolver from /application.yml or /application.properties.
     */
    public static PropertyResolver createPropertyResolver() {
        final Properties props = new Properties();
        // try load application.yml:
        try {
            Map<String, Object> ymlMap = YamlUtils.loadYamlAsPlainMap(CONFIG_APP_YAML);
            logger.info("load config: {}", CONFIG_APP_YAML);
            for (String key : ymlMap.keySet()) {
                Object value = ymlMap.get(key);
                if (value instanceof String strValue) {
                    props.put(key, strValue);
                }
            }
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                // try load application.properties:
                ClassPathUtils.readInputStream(CONFIG_APP_PROP, (input) -> {
                    logger.info("load config: {}", CONFIG_APP_PROP);
                    props.load(input);
                    return true;
                });
            }
        }
        return new PropertyResolver(props);
    }
}