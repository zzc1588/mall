package com.atguigu.gulimall.member.mq;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.gulimall.member.feign.MemberFeignService;
import com.atguigu.gulimall.member.service.MemberService;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-27 19:19
 **/
@Slf4j
@RabbitListener(queues = {"member.common.queue"})
@ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple.retry")
@Service
public class MemberMqListener {

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    MemberService memberService;
    private Integer init_attempt = 1;
    @Setter
    private Integer max_attempts;
    @RabbitHandler
    public void updateMemberRPC(OrderEntityTo to, Channel channel, Message message) throws IOException{
        try {
            System.out.println(to);
//            OrderEntityTo to = JSON.parseObject(obj.toString(), OrderEntityTo.class);
            System.out.println(message);
            memberService.updateIntegrationAndGrowth(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("消费失败次数{}", init_attempt);
            if(init_attempt == max_attempts){
                init_attempt = 0;
                /**
                 * 消息重试消费失败 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
                 * */
                System.out.println("消息重试消费失败，保存数据到数据库");
                String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
                memberFeignService.consumeMessageFail(Long.parseLong(messageId));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
            init_attempt++;
            throw e;
        }
    }
}
