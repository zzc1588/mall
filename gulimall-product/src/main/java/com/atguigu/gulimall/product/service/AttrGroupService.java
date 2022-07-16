package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.vo.AttrGroupWithAttrsVo;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogyId);

    List<AttrGroupWithAttrsVo> selectAttrGroupAndAttr(Long catelogId);

    List<AttrEntity> selectAttrGroupAttrs(Long attrgroupId);

    PageUtils selectAttrGroupNoRelationAttrs(Map<String, Object> params,Long attrgroupId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupAndAttrBySpuId(Long spuId, Long catalogId);

}

