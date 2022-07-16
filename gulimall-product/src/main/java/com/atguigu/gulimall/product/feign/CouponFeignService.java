package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.SpuReductionTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-13 0:19
 **/
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/spubounds/save")
    R savaSpuBounds(SpuBoundTo spuBoundTo);

    @RequestMapping("/coupon/skufullreduction/saveInfo")
    R savaSpuReduction(SpuReductionTo spuReductionTo);


}
