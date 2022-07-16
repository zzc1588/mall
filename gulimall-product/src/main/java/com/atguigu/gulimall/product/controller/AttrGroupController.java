package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.entity.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.entity.vo.AttrGroupWithAttrsVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 属性分组
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/attrgroup")
@Api(tags ="属性分组Controller")

public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    /**
     * 列表
     */
    @GetMapping("/list/{catelogyId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogyId") Long catelogyId){
//      PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogyId);
        return R.ok().put("page", page);
    }

    /**
     * 获取分组信息以及分组下面的属性信息
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupAndAttrByCatId(@PathVariable("catelogId") Long catelogId){

        List<AttrGroupWithAttrsVo> list = attrGroupService.selectAttrGroupAndAttr(catelogId);
        return R.ok().put("data",list);
    }

    /**
     * 获取《分组属性》关联的所有《属性》
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrGroupRelationAttrs(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> list = attrGroupService.selectAttrGroupAttrs(attrgroupId);
        return R.ok().put("data",list);
    }

    /**
     * 获取<未被>《属性分组》关联的《属性》
     * @param params
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getAttrGroupNoRelationAttrs(@RequestParam Map<String, Object> params,@PathVariable("attrgroupId") Long attrgroupId){
        PageUtils list = attrGroupService.selectAttrGroupNoRelationAttrs(params, attrgroupId);
        return R.ok().put("page",list);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 新增分组和属性的关联关系
     * @param relationVo
     * @return
     */
    @PostMapping("/attr/relation")
    public R saveRelation(@RequestBody List<AttrGroupRelationVo> relationVo){
        attrAttrgroupRelationService.saveBatch(relationVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody List<AttrGroupRelationVo> relationVo){
        attrAttrgroupRelationService.removeRelation(relationVo);
        return R.ok();
    }

}
