package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<BrandEntity> query = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            query.like(BrandEntity::getName, key)
                 .or()
                 .like(BrandEntity::getDescript,key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                query
        );
        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        //如果修改了品牌的name就需要同步更新关联了name的表
        if(!StringUtils.isEmpty(brand.getName())){
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }
        //TODO 更新所有关联的表
    }

}