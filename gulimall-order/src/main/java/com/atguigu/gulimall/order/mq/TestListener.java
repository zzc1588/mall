package com.atguigu.gulimall.order.mq;

import com.atguigu.gulimall.order.feign.OrderFeignService;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.retry.annotation.Retryable;
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
public class TestListener {
    private Integer count = 1;
    @Setter
    private Integer max_attempts ;
    @Autowired
    private OrderFeignService orderFeignService;
    @RabbitHandler
    public void listen(String object, Message message, Channel channel) throws IOException {
        Object lock = new Object();
        try {
            /**
             * 执行业务代码...
             * */
            System.out.println(object);
            System.out.println(message);
            Thread.currentThread().sleep(10);
            new Thread(() -> {
                synchronized (lock) {
                    try {
                        // 让当前线程休眠
                        lock.wait();
                        System.out.println("线程睡醒");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            int i=1/0;
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("签收失败", e);
            /**
             * 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
             * */
            System.out.println("count ======>"+count);
            if (count == max_attempts){
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            }
            count++;
            throw new RuntimeException("消息消费失败");
        }
    }

}
