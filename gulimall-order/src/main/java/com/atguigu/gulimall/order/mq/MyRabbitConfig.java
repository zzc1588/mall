package com.atguigu.gulimall.order.mq;

import com.atguigu.gulimall.order.exception.MqConfirmCallbackException;
import com.atguigu.gulimall.order.exception.MqReturnCallbackException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
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
        //设置确认回调
//        myRabbitTemplate.setConfirmCallback((correlationData,ack,cause) -> {
//            System.out.println("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
//        });
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
//            System.out.println("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
            if(!ack){
                throw new MqConfirmCallbackException("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
            }else {
                System.out.println("confirm...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
            }
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
            String msg = "Fail Message["+message+"]==>replyCode["+replyCode+"]" +
                    "==>replyText["+replyText+"]==>exchange["+exchange+"]==>routingKey["+routingKey+"]";
            throw new MqReturnCallbackException(msg);
        });
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
