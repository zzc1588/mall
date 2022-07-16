package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品三级分类
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RefreshScope
@RequestMapping("product/category")
@Api(tags ="商品三级分类Controller")

public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 列表
     */
    @GetMapping("/list/tree")
    public R list(){
        List<CategoryEntity> list = categoryService.listWithTree();
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity data = categoryService.getById(catId);
        return R.ok().put("data", data);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCategory(category);
        return R.ok();
    }
    /**
     * 批量修改菜单顺序，层级关系
     */
    @PostMapping("/update/sort")
    public R updateBatchId(@RequestBody CategoryEntity[] updateNodes){
        categoryService.updateBatchById(Arrays.asList(updateNodes));
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
