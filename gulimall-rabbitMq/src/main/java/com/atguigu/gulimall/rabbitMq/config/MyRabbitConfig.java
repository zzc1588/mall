package com.atguigu.gulimall.rabbitMq.config;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.rabbitMq.constant.MessageStatusConstant;
import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.atguigu.gulimall.rabbitMq.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-20 16:14
 **/
@Slf4j
@Configuration
public class MyRabbitConfig {
    private RabbitTemplate myRabbitTemplate;
    @Autowired
    private MqMessageService mqMessageService;
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        myRabbitTemplate = rabbitTemplate;
        myRabbitTemplate.setMessageConverter(messageConverter());
        myRabbitTemplate.setMandatory(true);
        //设置回调
        initRabbitTemplate();
        return myRabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就会回调
     *      1、spring.rabbitmq.publisher-confirms: true
     *      2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     *      1、spring.rabbitmq.publisher-returns: true
     *         spring.rabbitmq.template.mandatory: true
     *      2、设置确认回调ReturnCallback
     *
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以broker删除这个消息)
     *
     */
    // @PostConstruct  //MyRabbitConfig对象创建完成以后，执行这个方法
    public void initRabbitTemplate() {
        /**
         * 1、只要消息抵达Broker就ack=true
         * correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
         * ack：消息是否成功收到
         * cause：失败的原因
         */
        //设置确认回调
        myRabbitTemplate.setConfirmCallback((correlationData,ack,cause) -> {
            String messageId = correlationData.getId();
            if(ack){
                System.out.println("确认回调: 发送消息到Broker成功");
            }else {
                Integer status = MessageStatusConstant.MQ_CONFIRM_CALLBACK_ERROR.getCode();
                mqMessageService.updateStatusById(Long.parseLong(messageId),status);
                System.out.println("确认回调: 发送消息到Broker失败");
            }
            System.out.println("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
        });

        /**
         * 只要消息没有投递给指定的队列，就触发这个失败回调
         * message：投递失败的消息详细信息
         * replyCode：回复的状态码
         * replyText：回复的文本内容
         * exchange：当时这个消息发给哪个交换机
         * routingKey：当时这个消息用哪个路邮键
         */
        myRabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey) -> {
            Integer status = MessageStatusConstant.MQ_RETURN_CALLBACK_ERROR.getCode();
            String messageId = message.getMessageProperties().getCorrelationId();
            mqMessageService.updateOnReturnCallback(Long.parseLong(messageId),replyText,status);
            System.out.println("失败回调: 发送消息到queue失败");
            String msg = "Fail Message["+message+"]==>replyCode["+replyCode+"]" + "==>replyText["+replyText+"]==>exchange["+exchange+"]==>routingKey["+routingKey+"]";
            log.warn(msg);
        });
    }


    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true,false);
    }

    @Bean
    public Queue orderReleaseorderQueue(){
        return new Queue("order.release.order.queue",true,false,false);
    }

    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","order-event-exchange");
        args.put("x-dead-letter-routing-key","order.release");
        args.put("x-message-ttl",60000);
        return new Queue("order.delay.queue",true,false,false,args);
    }

    @Bean
    public Binding orderReleaseBinding(){
        return new Binding("order.release.order.queue"
                ,Binding.DestinationType.QUEUE
                ,"order-event-exchange"
                ,"order.release.#"
                ,null);
    }

//    @RabbitListener(queues = {"order.release.order.queue"})
//    public void initQueueAndExchange(){
//
//    }
    @Bean
    public Binding orderDelayBinding(){
        return new Binding("order.delay.queue"
                ,Binding.DestinationType.QUEUE
                ,"order-event-exchange"
                ,"order.create"
                ,null);
    }



}
