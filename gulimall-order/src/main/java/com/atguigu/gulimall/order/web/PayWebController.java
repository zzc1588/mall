package com.atguigu.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.order.config.alipay.AlipayTemplate;
import com.atguigu.gulimall.order.config.alipay.PayVo;
import com.atguigu.gulimall.order.constant.enume.OrderStatusEnum;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-28 11:35
 **/
@Controller
@Slf4j
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderService orderService;


    @ResponseBody
    @GetMapping(path = "/aliPayOrder",produces = "text/html;charset=utf-8")
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
        OrderEntity order = orderService.getOrderInfoByOrderSn(orderSn);
        if(order.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
            PayVo payVo = orderService.getOrderPayVo(orderSn);
            log.warn("传给支付宝的订单号orderSn: "+orderSn);
            String pay = alipayTemplate.pay(payVo);
            return pay;
        }else {
            return "<form name=\"submit_form\" method=\"get\" action=\"http://cart.gulimall.com/cart.html\">\n" +
                    "<input type=\"submit\" value=\"提交\" style=\"display:none\" >\n" +
                    "</form>\n" +
                    "<script>document.forms[0].submit();</script>";
        }
    }
}

// 响应为表单格式，可嵌入页面，具体以返回的结果为准
