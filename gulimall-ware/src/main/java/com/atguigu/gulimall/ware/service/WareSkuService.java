package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.vo.LockSkuResult;
import com.atguigu.gulimall.ware.entity.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.entity.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean LockOrderStock(WareSkuLockVo vo);

    void unlockStock(List<WareOrderTaskDetailEntity> list);
}

