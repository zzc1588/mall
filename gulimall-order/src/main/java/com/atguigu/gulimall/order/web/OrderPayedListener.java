package com.atguigu.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.alipay.AlipayTemplate;
import com.atguigu.gulimall.order.config.alipay.PayAsyncVo;
import com.atguigu.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-28 22:59
 **/
@RestController
@Slf4j
public class OrderPayedListener {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @PostMapping("/payed/notify")
    public String handleAliPayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
        // 只要收到支付宝的异步通知，返回 success 支付宝便不再通知
        // 获取支付宝POST过来反馈信息
        log.warn("支付宝异步收单消息抵达");
        //TODO 需要验签
        boolean signVerified = isSignVerified(request);
        if (signVerified) {
            String res = orderService.handleAliPayedResult(vo);
            return res;
        } else {
            return "error";
        }
    }

    private boolean isSignVerified(HttpServletRequest request) throws AlipayApiException {
        //TODO 需要验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        return signVerified;
    }
}
