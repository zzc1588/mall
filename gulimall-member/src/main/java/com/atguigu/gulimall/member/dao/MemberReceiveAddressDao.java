package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员收货地址
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
@Mapper
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {

    void updateAddressStatusById(@Param("id") Long id);
}
