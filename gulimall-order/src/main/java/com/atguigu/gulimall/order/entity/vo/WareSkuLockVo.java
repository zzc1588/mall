package com.atguigu.gulimall.order.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 18:50
 **/
@Data
public class WareSkuLockVo {
    private String orderSn;//订单号
    private List<OrderItemVo> locks;//徐娅萍锁住的所有库存信息
}
