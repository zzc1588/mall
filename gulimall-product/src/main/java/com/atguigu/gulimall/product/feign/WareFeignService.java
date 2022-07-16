package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 17:56
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);
}
