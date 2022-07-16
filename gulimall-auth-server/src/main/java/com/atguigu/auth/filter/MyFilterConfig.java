package com.atguigu.auth.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-19 16:47
 **/
//@Configuration
public class MyFilterConfig {
    @Bean
    public FilterRegistrationBean registFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new MyLoginFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("MyLoginFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
