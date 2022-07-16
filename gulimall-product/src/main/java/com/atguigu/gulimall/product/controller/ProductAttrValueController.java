package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * spu属性值
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/productattrvalue")
@Api(tags ="spu属性值Controller")

public class ProductAttrValueController {
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = productAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		ProductAttrValueEntity productAttrValue = productAttrValueService.getById(id);

        return R.ok().put("productAttrValue", productAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody ProductAttrValueEntity productAttrValue){
		productAttrValueService.save(productAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody ProductAttrValueEntity productAttrValue){
		productAttrValueService.updateById(productAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		productAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
