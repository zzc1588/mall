package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.entity.vo.MergeVo;
import com.atguigu.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.entity.vo.PurchaseItemDoneVo;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new LambdaQueryWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED.getCode())
                        .or()
                        .eq(PurchaseEntity::getStatus,WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        Boolean flag = false;
        if (purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
            flag = true;
        }else {
            //TODO 采购单必须是新建或已分配状态才能合并采购项
            PurchaseEntity purchase = this.getById(purchaseId);
            Integer status = purchase.getStatus();
            if(status.equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
                ||status.equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())){
                flag = true;
            }
        }

        if(flag){
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = mergeVo.getItems().stream().map(item -> {
                PurchaseDetailEntity detail = new PurchaseDetailEntity();
                detail.setId(item);
                detail.setPurchaseId(finalPurchaseId);
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        }

    }

    @Override
    public void receivedPurchase(List<Long> purchaseIds) {
        //修改采购单状态
        List<PurchaseEntity> purchaseIdsList = purchaseIds.stream().map(p -> {
            PurchaseEntity byId = this.getById(p);
            return byId;
        }).filter(f->{
            return f.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
                    ||f.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(purchaseIdsList);

        //修改采购单中的采购项状态  purchaseList已经筛选过 只包含0 ，1 两种状态的采购单
        purchaseIdsList.stream().forEach(p -> {
            LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseDetailEntity::getPurchaseId,p.getId());
            List<PurchaseDetailEntity> detailEntityList = purchaseDetailService.list(wrapper);

            List<PurchaseDetailEntity> purchaseDetailList = detailEntityList.stream()
                    .filter(f->{
                        //过滤状态只能为新建、已分配
                        return f.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode())
                                ||  f.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    })
                    .map(detail -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(detail.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailList);
        });



    }

    @Override
    public void donePurchaseItem(PurchaseDoneVo purchaseDoneVo) {
        //改变采购单状态
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchase = this.getById(id);

        //改变采购项状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
            if (purchase.getStatus().equals(WareConstant.PurchaseStatusEnum.RECEIVE.getCode())
                    &&byId.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode())){
                //只有采购单状态为已领取  并且  采购项的状态为正在采购才能进行下一步操作
                if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                    flag = false;//采购失败
                    detail.setStatus(item.getStatus());
                }else if (item.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode())){
                    //采购成功，修改采购项状态，并修改库存
                    detail.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                    //入库
                    PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                    wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
                }else {
                    //采购项不满足状态为  异常、采购中，则跳出
                    continue;
                }
                detail.setId(item.getItemId());
                updates.add(detail);
            }
        }
        if(updates!=null && updates.size()>0){
            purchaseDetailService.updateBatchById(updates);
        }


        //改变采购单状态
        Boolean flag2 = true;
        //获取该采购单下面的所有采购项
        List<PurchaseDetailEntity> detailList = purchaseDetailService.list(
                new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, id));
        for (PurchaseDetailEntity detail : detailList) {
            Integer status = detail.getStatus();
            //遍历所有采购项，如果没有达到全部为  完成状态则不修改采购单状态
            if (status.equals(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode())
                    ||status.equals(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode())
                    ||status.equals(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode())){
                flag2 = false;
            }
        }
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        if(!flag){
            //flag 为false 说明有异常
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }else if(flag2){
            // flag2 为true说明全部采购项为 已完成
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }
        this.updateById(purchaseEntity);



    }

}