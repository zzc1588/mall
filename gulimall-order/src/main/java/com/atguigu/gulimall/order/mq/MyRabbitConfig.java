package com.atguigu.gulimall.order.mq;

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
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true,false);
    }

    @Bean
    public Queue orderReleaseQueue(){
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

    @Bean
    public Binding orderDelayBinding(){
        return new Binding("order.delay.queue"
                ,Binding.DestinationType.QUEUE
                ,"order-event-exchange"
                ,"order.create"
                ,null);
    }
}
