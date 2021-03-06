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
            //TODO ???????????????????????????????????????????????????????????????
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
        //?????????????????????
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

        //????????????????????????????????????  purchaseList??????????????? ?????????0 ???1 ????????????????????????
        purchaseIdsList.stream().forEach(p -> {
            LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseDetailEntity::getPurchaseId,p.getId());
            List<PurchaseDetailEntity> detailEntityList = purchaseDetailService.list(wrapper);

            List<PurchaseDetailEntity> purchaseDetailList = detailEntityList.stream()
                    .filter(f->{
                        //???????????????????????????????????????
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
        //?????????????????????
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchase = this.getById(id);

        //?????????????????????
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
            if (purchase.getStatus().equals(WareConstant.PurchaseStatusEnum.RECEIVE.getCode())
                    &&byId.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode())){
                //?????????????????????????????????  ??????  ????????????????????????????????????????????????????????????
                if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                    flag = false;//????????????
                    detail.setStatus(item.getStatus());
                }else if (item.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode())){
                    //??????????????????????????????????????????????????????
                    detail.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                    //??????
                    PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                    wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
                }else {
                    //???????????????????????????  ??????????????????????????????
                    continue;
                }
                detail.setId(item.getItemId());
                updates.add(detail);
            }
        }
        if(updates!=null && updates.size()>0){
            purchaseDetailService.updateBatchById(updates);
        }


        //?????????????????????
        Boolean flag2 = true;
        //??????????????????????????????????????????
        List<PurchaseDetailEntity> detailList = purchaseDetailService.list(
                new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, id));
        for (PurchaseDetailEntity detail : detailList) {
            Integer status = detail.getStatus();
            //???????????????????????????????????????????????????  ???????????????????????????????????????
            if (status.equals(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode())
                    ||status.equals(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode())
                    ||status.equals(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode())){
                flag2 = false;
            }
        }
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        if(!flag){
            //flag ???false ???????????????
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }else if(flag2){
            // flag2 ???true???????????????????????? ?????????
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }
        this.updateById(purchaseEntity);



    }

}