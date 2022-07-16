package com.atguigu.gulimall.member.feign;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-07 21:28
 **/
@FeignClient("gulimall-gateway")
public interface MemberFeignService {

    @PostMapping("/api/rabbitMq/sendQueueMessage")
    R sendQueueMessage(@RequestBody QueueMessageTo queueMessageTo);

    @PostMapping("/api/rabbitMq/consumeMessageFail")
    R consumeMessageFail(@RequestParam("messageId") Long messageId);
}
