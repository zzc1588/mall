package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-19 22:25
 **/
@FeignClient("gulimall-gateway")
public interface WareFeignService {
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);

    @PostMapping("/api/rabbitMq/sendQueueMessage")
    R sendQueueMessage(@RequestBody QueueMessageTo queueMessageTo);

    @PostMapping("/api/rabbitMq/consumeMessageFail")
    R consumeMessageFail(@RequestParam("messageId") Long messageId);
}
