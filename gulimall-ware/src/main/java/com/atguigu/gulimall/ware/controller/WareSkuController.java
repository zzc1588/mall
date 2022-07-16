package com.atguigu.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.gulimall.ware.entity.vo.LockSkuResult;
import com.atguigu.gulimall.ware.entity.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.entity.vo.WareSkuLockVo;
import com.atguigu.gulimall.ware.exception.NoStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品库存
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 获取是否存在可用库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(vos);
    }

    @PostMapping("/lock/order")
    public R LockOrderStock(@RequestBody WareSkuLockVo vo){
        try {
            Boolean isStockLock = wareSkuService.LockOrderStock(vo);
            return R.ok().setData(isStockLock);
        }catch (NoStockException e){
            return R.error().put(BizCodeEnume.NO_STOCK_EXCEPTION.getMsg(),BizCodeEnume.NO_STOCK_EXCEPTION.getCode());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
