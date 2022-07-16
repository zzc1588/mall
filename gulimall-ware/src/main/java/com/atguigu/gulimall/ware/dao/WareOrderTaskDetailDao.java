package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存工作单
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
@Mapper
public interface WareOrderTaskDetailDao extends BaseMapper<WareOrderTaskDetailEntity> {

    Long updateTaskDetailStatus(@Param("id") Long id, @Param("status") Integer status);
}
