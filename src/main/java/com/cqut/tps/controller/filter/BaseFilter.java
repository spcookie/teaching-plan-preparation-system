package com.cqut.tps.controller.filter;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.io.IOException;

/**
 * @author Augenstern
 * @date 2021/12/29
 */
@WebFilter(filterName = "BaseFilter", urlPatterns = {"/input/*", "/export/*"})
public class BaseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        chain.doFilter(request, response);
    }
}
