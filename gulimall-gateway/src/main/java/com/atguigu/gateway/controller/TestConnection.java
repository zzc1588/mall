package com.atguigu.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-25 14:21
 **/
@Controller
public class TestConnection {
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "ok";
    }
}
