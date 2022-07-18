package com.atguigu.gulimall.member.interceptor;

import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.to.UserResponseTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 12:58
 **/
@Slf4j
public class MyMemberInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserResponseTo> loginUser = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("MyMemberInterceptor当前线程:{}",Thread.currentThread().getName());
        UserResponseTo attribute = (UserResponseTo)request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(antPathMatcher.match("/**/member/memberreceiveaddress/**", uri));
        list.add(antPathMatcher.match("/payed/notify", uri));
        list.add(antPathMatcher.match("/**/member/member/common/login", uri));
        list.add(antPathMatcher.match("/**/member/member/weixin/**", uri));
        list.add(antPathMatcher.match("/**/member/member/memberAlreadyExist/**", uri));
        list.add(antPathMatcher.match("/**/member/member/regist/**", uri));
        list.add(antPathMatcher.match("/swagger-ui/**", uri));
        for (Boolean b : list) {
            if(b){
                return true;
            }
        }


        if (attribute != null) {
            //把登录后用户的信息放在ThreadLocal里面进行保存
            loginUser.set(attribute);
            return true;
        } else {
            //未登录，返回登录页面
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            return false;
        }
    }
}
