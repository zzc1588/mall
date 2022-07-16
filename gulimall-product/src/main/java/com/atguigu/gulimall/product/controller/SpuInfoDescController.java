package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.SpuInfoDescService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * spu信息介绍
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/spuinfodesc")
@Api(tags ="spu信息介绍Controller")

public class SpuInfoDescController {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoDescService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{spuId}")
    public R info(@PathVariable("spuId") Long spuId){
		SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);

        return R.ok().put("spuInfoDesc", spuInfoDesc);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SpuInfoDescEntity spuInfoDesc){
		spuInfoDescService.save(spuInfoDesc);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody SpuInfoDescEntity spuInfoDesc){
		spuInfoDescService.updateById(spuInfoDesc);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] spuIds){
		spuInfoDescService.removeByIds(Arrays.asList(spuIds));

        return R.ok();
    }

}
