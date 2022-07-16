package com.atguigu.gulimall.order.entity.to;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 16:58
 **/
@Data
public class OrderCreateTo {
    private OrderEntity order;  //订单
    private List<OrderItemEntity> orderItems; //订单项
    private BigDecimal payPrice;  //应付价格
    private BigDecimal fare; //运费
}
