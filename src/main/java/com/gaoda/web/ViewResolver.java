package com.gaoda.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 为了处理ModelAndView，我们需要一个模板引擎，因此，抽象出ViewResolver接口：
 */
public interface ViewResolver {

    // 初始化ViewResolver:
    void init();

    // 渲染:
    void render(String viewName, Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

}