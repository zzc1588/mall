package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum) ;

    Long getSkuStock(@Param("id") Long id);

    List<Long> hasStockWareIdList(@Param("skuId") Long skuId, @Param("count") Integer count);

    Long lockSkuStock(@Param("wareId") Long wareId, @Param("count") Integer count, @Param("skuId") Long skuId);

    void unlockStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
