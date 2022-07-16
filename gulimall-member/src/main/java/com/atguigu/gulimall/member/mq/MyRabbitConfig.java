package com.atguigu.gulimall.member.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-20 16:14
 **/
@Configuration
public class MyRabbitConfig {
    private RabbitTemplate myRabbitTemplate;

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        myRabbitTemplate = rabbitTemplate;
        myRabbitTemplate.setMessageConverter(messageConverter());
        return myRabbitTemplate;
    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange memberEventExchange(){
        return new TopicExchange("member-event-exchange",true,false);
    }

    @Bean
    public Queue memberCommonQueue(){
        return new Queue("member.common.queue",true,false,false);
    }

    @Bean
    public Binding memberCommonBinding(){
        return new Binding("member.common.queue"
                ,Binding.DestinationType.QUEUE
                ,"member-event-exchange"
                ,"member.common"
                ,null);
    }

}
