package com.atguigu.gulimall.ware.mq;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.constant.enume.OrderTaskStatusEnum;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.feign.WareFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-26 18:32
 **/
@Service
@Slf4j
@RabbitListener(queues = {"stock.release.stock.queue"})
@ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple.retry")

public class WareSkuMqListener {
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService taskDetailService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private WareSkuService wareSkuService;
    private static final String WARE_TASK_STATUS = "ware_task_status";
    private Integer init_attempt = 1;
    @Setter
    private Integer max_attempts ;
    /**
     * 订单创建 订单待支付
     * 需要发送mq告诉库存服务回滚的情况
     * 1、订单超时30分钟,超时订单由订单模块主动发送消息
     * 2、订单被用户主动取消 （修改了订单状态0 1 2 ...）  由订单模块主动发送消息
     * 3、工作单对应的订单号不存在（说明订单业务在创建订单时出现异常）
     *
     * @param list
     */

    /**
     * 工作单对应订单号不存在，回滚解锁库存
     * @param taskDetailList
     */
//    @RabbitHandler
    @Transactional
    public void unlockStock(List<WareOrderTaskDetailEntity> taskDetailList, Channel channel, Message message) throws IOException {
        log.info("库存mq业务--开始检查订单业务是否异常");
        WareOrderTaskEntity task = wareOrderTaskService.getById(taskDetailList.get(0).getTaskId());
        //当前任务为未回滚状态才能执行回滚
        if (task!=null && task.getTaskStatus().equals(OrderTaskStatusEnum.CREATE_NEW.getCode())  ){
            String orderSn = task.getOrderSn();
            R r = orderFeignService.getByOrderSn(orderSn);
            if(r.getCode() == 0){
                Boolean hasOrder =(Boolean) r.get("hasOrder");
                if(!hasOrder){
                    log.info("库存mq业务--订单业务出现异常--订单不存在");
                    wareSkuService.unlockStock(taskDetailList);
                    Long count = wareOrderTaskService.updateTaskStatusById(task.getId(), OrderTaskStatusEnum.ORDER_EXCEPTION_UNLOCKED.getCode());
//                    if (count == 0){
//                        throw new RepeatReleaseException(task.getId());
//                    }
                }
            }else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            }
        }else {

        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }



    /**
     * version 2.0
     * 根据订单号回滚库存锁定  订单异常、订单关闭、订单超时，主动发出消息
     */
    @Transactional
    @RabbitHandler
    public void oderCancelUnlockRPC(HashMap<String, Object> map, Channel channel, Message message) throws IOException {
        try {
            /**
             * 执行业务代码...
             * */
            WareOrderTaskEntity task  = wareOrderTaskService.getByOrderSn(map.get("orderSn").toString());
            int status = Integer.parseInt(map.get(WARE_TASK_STATUS).toString());
            log.warn("订单号："+map.get("orderSn").toString()+"; 状态码："+status);
            if(task!=null && task.getTaskStatus().equals(OrderTaskStatusEnum.CREATE_NEW.getCode())) {
                List<WareOrderTaskDetailEntity> taskDetailList = taskDetailService.getByTaskId(task.getId());
                log.info("库存mq业务--订单取消或异常--库存回滚");
                //解锁库存，必须是幂等的
                wareSkuService.unlockStock(taskDetailList);
                //修改解锁状态，必须是幂等的
                wareOrderTaskService.updateTaskStatusById(task.getId(), status);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("消费失败次数{},错误信息{}", init_attempt);
            if(init_attempt == max_attempts){
                /**
                 * 记录日志、发送邮件、保存消息到数据库，落库之前判断如果消息已经落库就不保存
                 * */
                System.out.println("消息重试消费失败，保存数据到数据库");
                String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
                wareFeignService.consumeMessageFail(Long.parseLong(messageId));
                init_attempt=0;
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
            init_attempt++;
            throw e;
        }
    }
}
