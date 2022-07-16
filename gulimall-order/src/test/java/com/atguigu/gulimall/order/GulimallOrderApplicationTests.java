package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.dao.OrderReturnApplyDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class GulimallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void contextLoads() {
        DirectExchange exchange = new DirectExchange("gulimall.order.direct",true,false);
        amqpAdmin.declareExchange(exchange);
        log.info("创建交换机成功");
    }
    @Test
    void queue() {
        Queue queue = new Queue("gulimall.order.testQueue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("创建队列成功");
    }
    @Test
    void binding() {
        Binding binding = new Binding("gulimall.order.testQueue",
                Binding.DestinationType.QUEUE,
                                        "gulimall.order.direct",
                                        "test",null);
        amqpAdmin.declareBinding(binding);
        log.info("绑定成功");
    }

    @Test
    void sendMessage() {
        OrderEntity  order = new OrderEntity();
        order.setId(10000l);
        order.setDeliveryCompany("我是订单");
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderId(10000l);
        orderItemEntity.setId(9527l);
        orderItemEntity.setSkuAttrsVals("我是订单项属性！！");
        rabbitTemplate.convertAndSend("gulimall.order.direct","test",order);
        log.info("发送msg成功");
    }


}
