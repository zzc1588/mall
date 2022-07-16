package com.atguigu.gulimall.product.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.entity.vo.SpuSaveVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.service.SpuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * spu信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/spuinfo")
@Api(tags ="spu信息Controller")

public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    SearchFeignService searchFeignService;
    @Autowired
    SkuInfoService skuInfoService;
    /**
     * 商品上架
     */
    @PostMapping ("/{spuId}/up")
    public R spuUp(@PathVariable Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 商品下架
     */
    @PostMapping ("/soldOutSpuById/{spuId}")
    public R soldOutSpuById(@PathVariable("spuId") Long spuId) throws IOException {
        spuInfoService.soldOutSpuById(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByConfition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
		spuInfoService.saveInfo(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 根据skuId 获取spuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/spuInfo/{skuId}")
    public R getSpuBySkuId(@PathVariable("skuId") Long skuId){
        SpuInfoEntity spuInfo = spuInfoService.getSpuBySkuId(skuId);
        return R.ok().setData(spuInfo);
    }


}
