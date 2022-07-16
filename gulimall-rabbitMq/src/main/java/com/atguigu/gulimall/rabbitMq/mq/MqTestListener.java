package com.atguigu.gulimall.rabbitMq.mq;

import com.atguigu.gulimall.rabbitMq.constant.MessageStatusConstant;
import com.atguigu.gulimall.rabbitMq.retry.MyRetryable;
import com.atguigu.gulimall.rabbitMq.service.MqMessageService;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-04 14:52
 **/
@Slf4j
@Service
@RabbitListener(queues = "cn.news")
@ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple.retry")
public class MqTestListener {
    @Autowired
    private MqMessageService mqMessageService;
    @Setter
    private Integer max_attempts ;
    Integer count = 1;

    @RabbitHandler
    public void listen(String object, Message message, Channel channel) throws IOException {
        System.out.println("max_attempts:"+max_attempts);
        System.out.println("count:"+count);
        try {
            /**
             * 执行业务代码...
             * */
            System.out.println(object);
            System.out.println(message);
//            Thread.sleep(10000);
            int i = 1 / 0; //故意报错测试
        } catch (Exception e) {
            log.error("签收失败", e);
            /**
             * 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
             * */
            if(count == max_attempts){
                count = 0;
                System.out.println("重试次数耗尽：拒绝再次执行");
//                String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
//                mqMessageService.updateStatusById(Long.parseLong(messageId),MessageStatusConstant.MQ_CONSUMER_ERROR.getCode());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
            count++;
            System.out.println("重试次数count ======>"+count);
            throw new RuntimeException("消息消费失败");
        }
    }

}
