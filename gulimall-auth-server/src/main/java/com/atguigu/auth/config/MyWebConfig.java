package com.atguigu.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 18:20
 **/
@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    /**
     * 配置视图映射
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
