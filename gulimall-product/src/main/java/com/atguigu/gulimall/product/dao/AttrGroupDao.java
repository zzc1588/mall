package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupAndAttrBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}
