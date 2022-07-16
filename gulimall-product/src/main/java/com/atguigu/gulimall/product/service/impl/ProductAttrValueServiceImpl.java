package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.entity.vo.BaseAttrs;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.ProductAttrValueDao;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId) {
        LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId,spuId);
        List<ProductAttrValueEntity> list = this.list(wrapper);
        return list;
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        this.remove(new LambdaQueryWrapper<ProductAttrValueEntity>().eq(ProductAttrValueEntity::getSpuId,spuId));
        List<ProductAttrValueEntity> collect = entities.stream().map(entity -> {
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

    @Transactional
    @Override
    public void saveProductAttrValue(Long spuId, List<BaseAttrs> baseAttrs) {
//        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
//            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
//            productAttrValueEntity.setSpuId(spuId);
//            productAttrValueEntity.setAttrValue(item.getAttrValues());
//            productAttrValueEntity.setAttrId(item.getAttrId());
//            productAttrValueEntity.setQuickShow(item.getShowDesc());
//            AttrEntity attrEntity = attrService.getById(item.getAttrId());
//            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
//            return productAttrValueEntity;
//        }).collect(Collectors.toList());
//        this.saveBatch(collect);
    }

}