package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 属性&属性分组关联
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 22:50:32
 */
@RestController
@RequestMapping("product/attrattrgrouprelation")
@Api(tags = "属性&属性分组关联Controller")
public class AttrAttrgroupRelationController {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @ApiOperation("分页查询所有数据")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name="params", dataType="Map<String, Object>", required=true, value="分页查询参数")
    })
    //@RequiresPermissions("product:attrattrgrouprelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrAttrgroupRelationService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @ApiOperation("根据id查询详情")
    @GetMapping("/info/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name="id", dataType="Long", required=true, value="关联id")
    })
    public R info(@PathVariable("id") Long id){
		AttrAttrgroupRelationEntity attrAttrgroupRelation = attrAttrgroupRelationService.getById(id);

        return R.ok().put("attrAttrgroupRelation", attrAttrgroupRelation);
    }

    /**
     * 保存
     */
    @ApiOperation("保存实体")
    @PostMapping("/save")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="body", name="attrAttrgroupRelation", dataType="AttrAttrgroupRelationEntity", required=true, value="实体")
    })
    //@RequiresPermissions("product:attrattrgrouprelation:save")
    public R save(@RequestBody AttrAttrgroupRelationEntity attrAttrgroupRelation){
		attrAttrgroupRelationService.save(attrAttrgroupRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @ApiOperation("修改实体")
    @PostMapping("/update")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="body", name="attrAttrgroupRelation", dataType="AttrAttrgroupRelationEntity", required=true, value="实体")
    })
    //@RequiresPermissions("product:attrattrgrouprelation:update")
    public R update(@RequestBody AttrAttrgroupRelationEntity attrAttrgroupRelation){
		attrAttrgroupRelationService.updateById(attrAttrgroupRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="body", name="ids", dataType="Long", required=true, value="ids")
    })
    @ApiOperation("删除实体")
    @PostMapping("/delete")
    //@RequiresPermissions("product:attrattrgrouprelation:delete")
    public R delete(@RequestBody Long[] ids){
		attrAttrgroupRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
