package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.entity.vo.MergeVo;
import com.atguigu.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void receivedPurchase(List<Long> purchaseIds);

    void donePurchaseItem(PurchaseDoneVo purchaseDoneVo);
}

