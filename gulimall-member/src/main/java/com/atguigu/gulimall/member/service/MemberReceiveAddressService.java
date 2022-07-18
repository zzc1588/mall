package com.atguigu.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddressesByMemberId(Long memberId);

    /**
     * 新增收货地址
     * @param memberReceiveAddress
     */
    void saveMemberReceiveAddress(MemberReceiveAddressEntity memberReceiveAddress);
    /**
     * 根据主键和会员id删除
     */
    void deleteByIdAndMemberId(Long id );
}

