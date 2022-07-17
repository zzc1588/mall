package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.entity.vo.OrderItemVo;
import com.atguigu.gulimall.ware.entity.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.entity.vo.WareSkuLockVo;
import com.atguigu.gulimall.ware.constant.enume.OrderTaskDetailStatusEnum;
import com.atguigu.gulimall.ware.constant.enume.OrderTaskStatusEnum;
import com.atguigu.gulimall.ware.exception.NoStockException;
import com.atguigu.gulimall.ware.feign.WareFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private WareOrderTaskService  wareOrderTaskService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq(WareSkuEntity::getId,skuId);
        }
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq(WareSkuEntity::getWareId,wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WareSkuEntity::getWareId,wareId).eq(WareSkuEntity::getSkuId,skuId);
        List<WareSkuEntity> entityList = wareSkuDao.selectList(wrapper);
        if(entityList !=null && entityList.size()>0){
            wareSkuDao.addStock( skuId,  wareId,  skuNum);
        }else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            try{
                //TODO 通过不抛异常时的远程查询skuName失败时无需回滚
                //TODO  还有其他方法可以在出现异常时不回滚
                R info = wareFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }
            wareSkuDao.insert(wareSkuEntity);
        }
    }


    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> list = skuIds.stream().map(id -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询当前sku库存
            Long count = baseMapper.getSkuStock(id);
            vo.setSkuId(id);
            //这里数据库里面 如果锁定库存为null的话 查出来也是null
            vo.setHasStock(count==null?false:count > 0);
            return vo;
        }).collect(Collectors.toList());

        return list;
    }

    /**
     *(rollbackFor = NoStockException.class)
     * 默认只要是runtimeException都会回滚
     * @return
     */
    @Transactional
    @Override
    public Boolean LockOrderStock(WareSkuLockVo vo) {
        WareOrderTaskEntity wareOrderTask = new WareOrderTaskEntity();
        wareOrderTask.setOrderSn(vo.getOrderSn());
        wareOrderTask.setTaskStatus(OrderTaskStatusEnum.CREATE_NEW.getCode());
        wareOrderTaskService.save(wareOrderTask);

        //按照下单的收货地址，找到就近仓库
        //找到每个商品再哪个仓库
        List<OrderItemVo> itemVos = vo.getLocks();
        List<WareOrderTaskDetailEntity> list = new ArrayList<>();

        for (OrderItemVo item : itemVos) {
            Boolean skuIsLock = false;
            List<Long> wareIdList = this.baseMapper.hasStockWareIdList(item.getSkuId(), item.getCount());
            if(wareIdList == null || wareIdList.size() == 0){
                throw new NoStockException(item.getSkuId());
            }
            for (Long wareId : wareIdList) {
                Long count = this.baseMapper.lockSkuStock(wareId,item.getCount(),item.getSkuId());
                if (count > 0){
                    skuIsLock = true;
                    WareOrderTaskDetailEntity taskDetail =
                            new WareOrderTaskDetailEntity(null,item.getSkuId(),"",item.getCount(),wareOrderTask.getId(),wareId, OrderTaskDetailStatusEnum.TASK_DETAIL_LOCKED.getCode());
                    wareOrderTaskDetailService.save(taskDetail);
                    list.add(taskDetail);
                    break;
                }
            }
            if (!skuIsLock){
                throw new NoStockException(item.getSkuId());
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void unlockStock(List<WareOrderTaskDetailEntity> list) {
        for (WareOrderTaskDetailEntity taskDetail : list) {
            Long count = wareOrderTaskDetailService.updateTaskDetailStatus(taskDetail.getId(), OrderTaskDetailStatusEnum.TASK_DETAIL_UNLOCKED.getCode());
            if(count>0){
                this.baseMapper.unlockStock(taskDetail.getWareId(),taskDetail.getSkuId(),taskDetail.getSkuNum());
            }
        }

    }

}