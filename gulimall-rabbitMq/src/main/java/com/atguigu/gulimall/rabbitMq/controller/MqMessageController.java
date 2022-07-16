package com.atguigu.gulimall.rabbitMq.controller;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.SnowflakeIdWorker;
import com.atguigu.gulimall.rabbitMq.constant.IdWorkerConstant;
import com.atguigu.gulimall.rabbitMq.constant.MessageStatusConstant;
import com.atguigu.gulimall.rabbitMq.mq.SendMessage;
import com.atguigu.gulimall.rabbitMq.service.MqMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:37
 **/
@RestController
@RequestMapping("/rabbitMq")
public class MqMessageController {
    @Autowired
    private SendMessage sendMessage;
    @Autowired
    private MqMessageService mqMessageService;


    @PostMapping("/sendQueueMessage")
    public R sendQueueMessage(@RequestBody QueueMessageTo queueMessageTo){
        try {
            sendMessage.sendMqMessage(queueMessageTo);
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
    }

    @PostMapping("/consumeMessageFail")
    public R consumeMessageFail(@RequestParam("messageId") Long messageId){
        mqMessageService.updateStatusById(messageId, MessageStatusConstant.MQ_CONSUMER_ERROR.getCode());
        return R.ok();
    }




}
