package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.to.UserResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 12:58
 **/
public class MyOrderInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserResponseTo> loginUser = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserResponseTo attribute = (UserResponseTo)request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(antPathMatcher.match("/**/order/order/**/getByOrderSn", uri));
        list.add(antPathMatcher.match("/payed/notify", uri));
        list.add(antPathMatcher.match("/**/order/order/**/getOrderInfoByOrderSn", uri));
//        list.add(antPathMatcher.match("/**/order/order/listOrderWithItem", uri));
        for (Boolean b : list) {
            if(b){
                return true;
            }
        }


        if (attribute != null) {
            //把登录后用户的信息放在ThreadLocal里面进行保存
            loginUser.set(attribute);
            request.getSession().setAttribute("gulimall-order","gulimall-order");

            return true;
        } else {
            //未登录，返回登录页面
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            // session.setAttribute("msg", "请先进行登录");
            // response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
