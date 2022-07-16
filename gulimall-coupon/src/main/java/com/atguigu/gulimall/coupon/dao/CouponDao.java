package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:09:02
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
