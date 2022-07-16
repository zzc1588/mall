package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.io.IOException;
import java.util.Map;

/**
 * spu信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SpuSaveVo spuSaveVo);

    PageUtils queryPageByConfition(Map<String, Object> params);

    void up(Long spuId);

    /**
     * 订单模块用。根据skuid获取spuInfo
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuBySkuId(Long skuId);

    /**
     * 根据spuId 下架商品
     * @param spuId
     */
    void soldOutSpuById(Long spuId) throws IOException;
}

