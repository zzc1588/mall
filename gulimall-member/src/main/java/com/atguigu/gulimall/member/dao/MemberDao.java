package com.atguigu.gulimall.member.dao;

import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    void updateIntegrationAndGrowth(@Param("to") OrderEntityTo to);
}
