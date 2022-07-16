package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:43:51
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
