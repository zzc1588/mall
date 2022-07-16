package com.atguigu.gulimall.rabbitMq.service;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:24
 **/
public interface MqMessageService extends IService<MqMessageEntity> {
    void updateStatusById(Long messageId, Integer status);

    void updateOnReturnCallback(Long messageId, String replyText, Integer status);

}
