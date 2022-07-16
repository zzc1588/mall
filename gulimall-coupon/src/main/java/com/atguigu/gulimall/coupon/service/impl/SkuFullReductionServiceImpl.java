package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.MemberPrice;
import com.atguigu.common.to.SpuReductionTo;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService ladderService;
    @Autowired
    private MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveInfo(SpuReductionTo spuReductionTo) {
        // sms_sku_ladder  <满几件-打折表>
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(spuReductionTo,skuLadderEntity);
        if(skuLadderEntity.getFullCount() > 0){
            ladderService.save(skuLadderEntity);
        }
        //sms_sku_full_reduction <满多少钱-减多少钱的表>
        SkuFullReductionEntity skuFullReductionEntity  = new SkuFullReductionEntity();
        BeanUtils.copyProperties(spuReductionTo,skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){
            this.save(skuFullReductionEntity);
        }
        //sms_member_price  <会员特殊价格表>
        List<MemberPrice> list = spuReductionTo.getMemberPrice();
        if(list != null && list.size()>0 ){
            List<MemberPriceEntity> collect = list.stream().map(item -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(spuReductionTo.getSkuId());
                memberPriceEntity.setMemberPrice(item.getPrice());
                memberPriceEntity.setMemberLevelId(item.getId());
                memberPriceEntity.setMemberLevelName(item.getName());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            })
            .filter(entity->entity.getMemberPrice().compareTo(new BigDecimal("0")) == 1)
            .collect(Collectors.toList());
            if(collect != null && collect.size() > 0){
                memberPriceService.saveBatch(collect);
            }
        }


    }

}