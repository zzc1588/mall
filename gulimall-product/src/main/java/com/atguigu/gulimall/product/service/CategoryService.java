package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.vo.web.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    /**
     * 找到分类的完整路径[父，子，孙]([1,22,123])
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCategory(CategoryEntity category);

    List<CategoryEntity> getLevel1Catetorys();

    Map<String, List<Catelog2Vo>> getCatalogJson();
}

