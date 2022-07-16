package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:43:51
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
    Long cancelOrder(@Param("orderSn") String orderSn, @Param("status") Integer status);

    void updateOrderStatus(@Param("orderSn") String out_trade_no, @Param("status") Integer status);
}
