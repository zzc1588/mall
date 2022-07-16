package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


/**
 * 品牌
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController

@Validated
@RequestMapping("product/brand")
@Api(tags ="品牌Controller")

public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改显示状态
     */
    @PostMapping("/updateShowStatus")
    public R updateShowStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {
        LambdaUpdateWrapper<BrandEntity> query = new LambdaUpdateWrapper<>();
        query.eq(BrandEntity::getBrandId,brand.getBrandId())
                .set(BrandEntity::getShowStatus,brand.getShowStatus());
        brandService.update(null,query);
        return R.ok();
    }

//    @RequestMapping("/test")
//    public R test(@Min(value = 0,message = "地址不能为空！") @RequestParam(name = "address") Integer Address){
//        return R.ok();
//    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
