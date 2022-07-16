package com.atguigu.gulimall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * sku信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 22:50:32
 */
@RestController
@RequestMapping("product/skuinfo")
@Api(tags ="sku信息Controller")

public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;


    @GetMapping("/{skuId}/newSkuPrice")
    public String getNewSkuPriceById(@PathVariable("skuId") Long skuId){
        BigDecimal newPrice = skuInfoService.getNewSkuPriceById(skuId);
        return newPrice.toString();
    }

    /**
     * 根据spuId 获取 所有sku
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuListBySpuId")
    public R getSkuListBySpuId(@RequestParam("spuId")Long spuId){
        List<SkuInfoEntity> skuList = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIds = skuList.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        return R.ok().setData(skuList).setData("skuIds",skuIds);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));
        return R.ok();
    }



}
