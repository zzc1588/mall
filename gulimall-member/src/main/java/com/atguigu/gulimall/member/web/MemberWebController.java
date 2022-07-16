package com.atguigu.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.feign.OrderFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-28 22:15
 **/
@Controller
@Slf4j
public class MemberWebController {
    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",
            defaultValue = "1") Integer pageNum,
                                  Model model){
        HashMap<String, Object> param = new HashMap<>();
        param.put("page",pageNum.toString());
        R r = orderFeignService.listOrderWithItem(param);
        if(r.getCode() == 0){
            model.addAttribute("orders", r);
            log.warn( JSON.toJSONString(r));
        }
        return "orderList";
    }
}
