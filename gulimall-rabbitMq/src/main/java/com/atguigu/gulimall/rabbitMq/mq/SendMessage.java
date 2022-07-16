package com.atguigu.gulimall.rabbitMq.mq;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.SnowflakeIdWorker;
import com.atguigu.gulimall.rabbitMq.constant.IdWorkerConstant;
import com.atguigu.gulimall.rabbitMq.constant.MessageStatusConstant;
import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.atguigu.gulimall.rabbitMq.service.MqMessageService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 17:10
 **/
@Service
public class SendMessage {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private SnowflakeIdWorker idWorker;

    public void sendMqMessage(QueueMessageTo queueMessageTo) {
        //id
        Long messageId = idWorker.nextId();
        CorrelationData correlationData = new CorrelationData(messageId.toString());
        MqMessageEntity mqMessageEntity = new MqMessageEntity();
        mqMessageEntity.setMessageId(messageId);
        System.out.println("messageId==================》》》"+messageId);

        BeanUtils.copyProperties(queueMessageTo,mqMessageEntity);
        mqMessageEntity.setContent(queueMessageTo.getContent().toString());
        mqMessageEntity.setMessageStatus(MessageStatusConstant.MQ_MESSAGE_CREATE.getCode());

        mqMessageService.save(mqMessageEntity);

        System.out.println(queueMessageTo);
        System.out.println(queueMessageTo.getClassType());
        if (queueMessageTo.getClassType() != null){
            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader("__ContentTypeId__", queueMessageTo.getClassType());
                    message.getMessageProperties().setHeader("__KeyTypeId__", queueMessageTo.getClassType());
                    message.getMessageProperties().setHeader("__TypeId__", queueMessageTo.getClassType());
                    return message;
                }
            };
            rabbitTemplate.convertAndSend(queueMessageTo.getToExchange(),queueMessageTo.getRoutingKey(),queueMessageTo.getContent(),messagePostProcessor,correlationData);
        }else {
            rabbitTemplate.convertAndSend(queueMessageTo.getToExchange(),queueMessageTo.getRoutingKey(),queueMessageTo.getContent(),correlationData);
        }
//        try {
//            String s = new String(message.getBody(), "utf-8");
//            mqMessageEntity.setContent(JSON.toJSONString(s));
//            System.out.println();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }
}
