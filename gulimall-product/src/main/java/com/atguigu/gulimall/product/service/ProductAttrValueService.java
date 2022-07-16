package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.vo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);

    void saveProductAttrValue(Long spuId, List<BaseAttrs> baseAttrs);
}

