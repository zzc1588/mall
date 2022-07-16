package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 0:27
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    /**
     * 用户信息
     */
    @RequestMapping("/member/member/info/{id}")
    R info(@PathVariable("id") Long id);

    /**
     * 地址信息
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
