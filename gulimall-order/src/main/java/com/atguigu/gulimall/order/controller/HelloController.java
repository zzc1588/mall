package com.atguigu.gulimall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-21 0:49
 **/
@Controller
public class HelloController {

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){
        return page;
    }
}
