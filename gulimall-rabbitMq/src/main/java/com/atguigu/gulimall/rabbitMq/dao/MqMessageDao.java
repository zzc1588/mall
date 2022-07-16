package com.atguigu.gulimall.rabbitMq.dao;

import com.atguigu.gulimall.rabbitMq.entity.MqMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {
    void updateStatusById(@Param("messageId") Long messageId, @Param("status") Integer status);

    void updateOnReturnCallback(@Param("messageId") Long messageId, @Param("replyText") String replyText, @Param("status") Integer status);
}
