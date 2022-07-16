package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.vo.Skus;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    PageUtils queryPageByCondition(Map<String, Object> params);


    void saveSkuInfo(Long brandId,Long catalogId,Long spuId, List<Skus> skusList);

    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;

    /**
     * 订单模块，查询最新商品价格
     * @param skuId
     * @return
     */
    BigDecimal getNewSkuPriceById(Long skuId);
}

