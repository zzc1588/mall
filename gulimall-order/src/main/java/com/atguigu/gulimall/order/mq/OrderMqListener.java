package com.atguigu.gulimall.order.mq;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.gulimall.order.config.alipay.AlipayTemplate;
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
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: TODO
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
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderFeignService orderFeignService;
    private Integer init_attempt = 1;
    @Setter
    private Integer max_attempts ;

    /**
     * 订单超时/取消,修改订单状态，解锁库存，幂等
     * @param map
     * @param channel
     * @param message
     * @throws IOException
     */
//    @RabbitHandler
    public void orderTimeOut(HashMap<String, Object> map, Channel channel, Message message) throws IOException {
        log.warn("订单业务执行mq延时消息，检查订单状态=> 支付/未支付");
        try {
            Object orderSn = map.get("orderSn");
            Long count = orderService.cancelOrder(orderSn.toString(), OrderStatusEnum.CANCLED.getCode());
            if (count > 0 ){
                log.warn("订单超时 或 主动取消");
                log.warn("执行修改订单状态 和 解锁库存");
                for (Object value : map.values()) {
                    log.warn(value.toString());
                }
                log.warn("关闭订单时传的订单号："+orderSn);
                alipayTemplate.close(orderSn.toString(),null);
            }
            rabbitTemplate.convertAndSend("stock-event-exchange","stock.release.#",map,new CorrelationData());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }

    }


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
                System.out.println("检查结果为：订单《超时》 或 《主动取消》 ，执行《修改订单状态》 并 《解锁库存》");
                //在网页的关单需要用户登录账号后才会创建订单号
//                alipayTemplate.close(orderSn.toString(),null);
                QueueMessageTo queueMessageTo = new QueueMessageTo();
                queueMessageTo.setToExchange("stock-event-exchange");
                queueMessageTo.setRoutingKey("stock.release.#");
                queueMessageTo.setContent(map);
                //发送解锁库存mq，解锁库存方法必须是幂等的,先发送mq，再修改数据库状态
                orderFeignService.sendQueueMessage(queueMessageTo);
//                System.out.println("睡眠1000秒");
//                Thread.sleep(1000000);
                Long count = orderService.cancelOrder(orderSn.toString(), OrderStatusEnum.CANCLED.getCode());
            }else {
                System.out.println("检查结果为：订单已支付");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("消费失败次数{},错误信息{}", init_attempt);
            if(init_attempt == max_attempts){
                init_attempt = 0;
                /**
                 * 消息重试消费失败 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
                 * */
                System.out.println("消息重试消费失败，保存数据到数据库");
                String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
                orderFeignService.consumeMessageFail(Long.parseLong(messageId));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
            init_attempt++;
            throw e;
        }

    }
}
