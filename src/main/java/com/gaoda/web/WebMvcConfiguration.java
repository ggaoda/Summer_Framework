package com.gaoda.web;


import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.*;
import jakarta.servlet.ServletContext;

import java.util.Objects;

/**
 * 为了简化Web应用程序配置，我们提供一个WebMvcConfiguration配置：
 */
@Configuration
public class WebMvcConfiguration {

    /**
     * 默认创建一个ViewResolver和ServletContext，注意ServletContext本身实际上是由Servlet容器提供的，
     * 但我们把它放入IoC容器，是因为许多涉及到Web的组件，如ViewResolver，需要注入ServletContext，才能从指定配置加载文件。
     */

    private static ServletContext servletContext = null;

    /**
     * Set by web listener.
     */
    public static void setServletContext(ServletContext ctx) {
        servletContext = ctx;
    }

    @Bean(initMethod = "init")
    ViewResolver viewResolver( //
                               @Autowired ServletContext servletContext, //
                               @Value("${summer.web.freemarker.template-path:/WEB-INF/templates}") String templatePath, //
                               @Value("${summer.web.freemarker.template-encoding:UTF-8}") String templateEncoding) {
        return new FreeMarkerViewResolver(servletContext, templatePath, templateEncoding);
    }

    @Bean
    ServletContext servletContext() {
        return Objects.requireNonNull(servletContext, "ServletContext is not set.");
    }
}