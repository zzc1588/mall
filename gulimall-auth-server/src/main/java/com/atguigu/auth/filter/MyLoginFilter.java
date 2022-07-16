package com.atguigu.auth.filter;

import com.atguigu.common.constant.AuthServiceConstant;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-19 16:33
 **/
@WebFilter("/*")
@Order(1)
public class MyLoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();
        Object attribute = request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);

        HttpServletResponse response = (HttpServletResponse) resp;
        if ((uri.contains("/login.html") || uri.contains("reg.html"))  &&  attribute != null){
            response.sendRedirect("http://gulimall.com/");
        }
        chain.doFilter(req, resp);
    }

    public void init(MyFilterConfig config) throws ServletException {

    }

    public void destroy() {
    }

}
