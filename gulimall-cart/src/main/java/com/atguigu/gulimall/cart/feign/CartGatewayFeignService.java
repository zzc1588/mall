package com.atguigu.gulimall.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-16 0:32
 **/
@FeignClient("gulimall-gateway")
public interface CartGatewayFeignService {

    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/skusaleattrvalue/stringlist/{skuId}")
    R stringList(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/skuinfo/{skuId}/newSkuPrice")
    String getNewSkuPriceById(@PathVariable("skuId") Long skuId);
}
