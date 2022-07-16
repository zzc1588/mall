package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 修改状态
     * @param id
     * @param code
     * @return
     */
    Long updateTaskStatusById(Long id, Integer code);

    /**
     * 根据订单号获取详情
     * @param orderSn
     * @return
     */
    WareOrderTaskEntity getByOrderSn(String orderSn);



}

