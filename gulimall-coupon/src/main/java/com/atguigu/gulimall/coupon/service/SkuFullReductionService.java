package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.to.SpuReductionTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:09:02
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SpuReductionTo spuReductionTo);

}

