package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.to.UserInfoTo;
import com.atguigu.common.to.UserResponseTo;
import com.atguigu.gulimall.cart.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 23:08
 **/
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        //获取session中的用户信息，有值说明已登录，无值分配临时用户身份
        HttpSession session = request.getSession();
        UserResponseTo userResponseTo = (UserResponseTo)session.getAttribute(AuthServiceConstant.LOGIN_USER);
        if(userResponseTo!=null){
            userInfoTo.setUserId(userResponseTo.getId());
        }

        //session无值分配临时身份,获取cookie查看是否有只，有只则不用再分配，无值则分配临时用户（分配临时用户在post方法）
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setIsTemp(true);
                }
            }

        }
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 分配临时用户
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        request.getSession().setAttribute("gulimall-cart","gulimall-cart");

        if(userInfoTo!=null && !userInfoTo.getIsTemp()){
            //没用临时用户身份则分配
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
