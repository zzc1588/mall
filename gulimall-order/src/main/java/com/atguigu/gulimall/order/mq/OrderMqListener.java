package com.atguigu.gulimall.order.mq;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.gulimall.order.constant.enume.OrderStatusEnum;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.feign.OrderFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: MQ订单关闭
 * @DateTime: 2022-06-27 19:19
 **/
@Slf4j
@RabbitListener(queues = {"order.release.order.queue"})
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple.retry")
public class OrderMqListener {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFeignService orderFeignService;
    private Integer init_attempt = 1;
    @Setter
    private Integer max_attempts ;

    /**
     * versoin 2.0
     * 订单超时/取消,修改订单状态，解锁库存，幂等
     * @param map
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    @Transactional
    public void orderTimeOutRPC(HashMap<String, Object> map, Channel channel, Message message) throws IOException, InterruptedException {
        log.warn("订单业务执行mq延时消息，检查订单状态=> 支付/未支付");
        try {
            /**
             * 执行业务代码...
             * */
            Object orderSn = map.get("orderSn");
            OrderEntity order = orderService.getOrderInfoByOrderSn(orderSn.toString());
            if (order.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode()) ){
                log.info("检查结果为：订单《超时》 或 《主动取消》 ，执行《修改订单状态》 并 《解锁库存》");
                //在网页的关单需要用户登录账号后才会创建订单号
                QueueMessageTo queueMessageTo = new QueueMessageTo();
                queueMessageTo.setToExchange("stock-event-exchange");
                queueMessageTo.setRoutingKey("stock.release.#");
                queueMessageTo.setContent(map);
                //发送解锁库存mq，解锁库存方法必须是幂等的,先发送mq，再修改数据库状态
                orderFeignService.sendQueueMessage(queueMessageTo);
                orderService.cancelOrder(orderSn.toString(), OrderStatusEnum.CANCLED.getCode());
            }else {
                log.info("检查结果为：订单已支付");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("消费失败次数{}", init_attempt);
            if(init_attempt.equals(max_attempts)){
                init_attempt = 0;
                /**
                 * 消息重试消费失败 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
                 * */
                log.error("消息重试消费失败，保存数据到数据库");
                String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
                orderFeignService.consumeMessageFail(Long.parseLong(messageId));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
            init_attempt++;
            throw e;
        }

    }
}
