package com.atguigu.gulimall.rabbitMq.controller;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.SnowflakeIdWorker;
import com.atguigu.common.valid.SelectGroup;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.gulimall.rabbitMq.constant.IdWorkerConstant;
import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.atguigu.gulimall.rabbitMq.entity.TestEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-05 0:21
 **/
@RestController
public class TestController {
    @Autowired
    private SnowflakeIdWorker idWorker;

    /**
     * 请求体JSON参数校验
     * @param queueMessageTo
     * @return
     */
    @PostMapping("/testIdWorker")
    public long testIdWorker(@RequestBody @Validated({SelectGroup.class, Default.class}) QueueMessageTo queueMessageTo){
        System.out.println(queueMessageTo.toString());
        long l = idWorker.nextId();
        System.out.println(l);
        return l;
    }

    /**
     * 表单参数校验
     * @param queueMessageTo
     * @return
     */
    @PostMapping("/submit")
    public long submit( @Validated({SelectGroup.class, Default.class}) QueueMessageTo queueMessageTo){
        System.out.println(queueMessageTo.toString());
        long l = idWorker.nextId();
        System.out.println(l);
        return l;
    }
    /**
     * 嵌套对象参数校验
     * @param testEntity
     * @return
     */
    @RabbitHandler
    @PostMapping("/testValidated")
    public void testValidated(@RequestBody @Validated({SelectGroup.class, Default.class}) TestEntity testEntity){
            System.out.println(testEntity.toString());
    }


}
