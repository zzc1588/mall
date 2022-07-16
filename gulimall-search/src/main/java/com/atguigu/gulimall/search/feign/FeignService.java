package com.atguigu.gulimall.search.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-28 19:11
 **/
@FeignClient("gulimall-gateway")
public interface FeignService {
    /**
     * 获取属性信息
     */
    @GetMapping("/api/product/attr/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    R attrInfo(@PathVariable("attrId") Long attrId);

}
