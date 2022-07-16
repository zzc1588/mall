package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-28 21:57
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/listOrderWithItem")
    R listOrderWithItem(@RequestBody Map<String, Object> params);
}
