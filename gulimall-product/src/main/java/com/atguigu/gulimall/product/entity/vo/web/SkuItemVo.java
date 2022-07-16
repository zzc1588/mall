package com.atguigu.gulimall.product.entity.vo.web;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO 商品详情页面展示的详细内容
 * @DateTime: 2022-05-29 21:09
 **/
@Data
public class SkuItemVo {
    //sku基本信息
    private SkuInfoEntity info;
    //sku的图片信息
    private List<SkuImagesEntity> images;
    //spu销售组合
    List<SkuItemSaleAttrVo> saleAttr;
    //spu的介绍图片
    private SpuInfoDescEntity desc;
    //参数规格信息
    private List<SpuItemAttrGroupVo> groupAttrs;
    @Data //销售属性
    @ToString
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        //属性值
        private List<AttrValueAndSkuIdVo> attrValues;
    }

    @Data
    @ToString
    public static class AttrValueAndSkuIdVo{
        private String attrValue;
        private String skuIds;
    }

    @Data
    //spu商品基本信息（分组信息）
    @ToString
    public static class SpuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;

    }

    @Data
    @ToString
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }
}
