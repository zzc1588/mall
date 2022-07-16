package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/categorybrandrelation")
@Api(tags ="品牌分类关联Controller")

public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam Long brandId){
        LambdaQueryWrapper<CategoryBrandRelationEntity> query = new LambdaQueryWrapper<>();
        query.eq(CategoryBrandRelationEntity::getBrandId,brandId);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(query);
        return R.ok().put("data", list);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 获取分类下面关联的所有品牌
     * @param catId
     * @return
     */
    @GetMapping("/brands/list")
    public R getBrandsListByCatId(@RequestParam(value = "catId",required = true) Long catId){
        LambdaQueryWrapper<CategoryBrandRelationEntity> query = new LambdaQueryWrapper<>();
        query.eq(CategoryBrandRelationEntity::getCatelogId,catId);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(query);
        return R.ok().put("data",list);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
