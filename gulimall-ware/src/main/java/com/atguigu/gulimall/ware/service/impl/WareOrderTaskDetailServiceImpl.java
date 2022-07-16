package com.atguigu.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareOrderTaskDetailDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 修改工作单详情状态 1 锁定， 2 解锁， 3 扣除
     * @param id
     * @param status
     */
    @Override
    public Long updateTaskDetailStatus(Long id, Integer status) {
        return this.baseMapper.updateTaskDetailStatus(id,status);
    }

    @Override
    public List<WareOrderTaskDetailEntity> getByTaskId(Long id) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<WareOrderTaskDetailEntity>().eq(WareOrderTaskDetailEntity::getTaskId,id));
    }

}