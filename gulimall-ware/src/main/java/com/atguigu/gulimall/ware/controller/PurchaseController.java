package com.atguigu.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.ware.entity.vo.MergeVo;
import com.atguigu.gulimall.ware.entity.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 采购信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 合并采购单
     * @param mergeVo
     * @return
     */
    @RequestMapping("/merge")
    public R mergePurchase(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }
    /**
     * 领取采购单
     * @param purchaseIds
     * @return
     */
    @RequestMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> purchaseIds){
        purchaseService.receivedPurchase(purchaseIds);
        return R.ok();
    }
    /**
     * 完成采购
     * @param purchaseDoneVo
     * @return
     */
    @RequestMapping("/done")
    public R donePurchaseItem(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.donePurchaseItem(purchaseDoneVo);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
