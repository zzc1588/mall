package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.order.config.alipay.PayAsyncVo;
import com.atguigu.gulimall.order.config.alipay.PayVo;
import com.atguigu.gulimall.order.entity.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.entity.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.entity.vo.SubmitOrderResponseVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:43:51
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo orderConfirmData() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号判断订单是否存在
     * @param orderSn
     * @return
     */
    Long getByOrderSn(String orderSn);

    /**
     * 根据订单号获取订单详情
     * @param orderSn
     * @return
     */
    OrderEntity getOrderInfoByOrderSn(String orderSn);

    /**
     * 修改订单状态
     * @param orderSn
     * @param status
     * @return
     */
    Long cancelOrder(String orderSn, Integer status);


    PayVo getOrderPayVo(String orderSn);

    /**
     * 用户订单列表和订单详情
     */
    PageUtils listOrderWithItem(Map<String, Object> params);

    String handleAliPayedResult(PayAsyncVo vo);
}


