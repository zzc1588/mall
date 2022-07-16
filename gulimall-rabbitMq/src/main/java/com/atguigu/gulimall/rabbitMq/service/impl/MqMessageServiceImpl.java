package com.atguigu.gulimall.rabbitMq.service.impl;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.gulimall.rabbitMq.dao.MqMessageDao;
import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.atguigu.gulimall.rabbitMq.service.MqMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:33
 **/
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

    @Override
    public void updateStatusById(Long messageId, Integer status) {
        this.baseMapper.updateStatusById(messageId,status);
    }

    @Override
    public void updateOnReturnCallback(Long messageId, String replyText, Integer status) {
        this.baseMapper.updateOnReturnCallback(messageId,replyText,status);
    }
}
