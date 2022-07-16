package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-27 11:16
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 判断订单是否存在
     * @param orderSn
     * @return
     */
    @GetMapping("/order/order/{orderSn}/getByOrderSn")
    R getByOrderSn(@PathVariable("orderSn") String orderSn);
    /**
     * 获取订单详情
     * @param orderSn
     * @return
     */
    @GetMapping("/order/order/{orderSn}/getOrderInfoByOrderSn")
    R getOrderInfoByOrderSn(@PathVariable("orderSn") String orderSn);
    /**
     * 取消订单
     * @param orderSn
     * @param status
     * @return
     */
    @PostMapping("/order/order/cancelOrder")
    R cancelOrder(@RequestParam("orderSn") String orderSn,@RequestParam("status") Integer status);
}
