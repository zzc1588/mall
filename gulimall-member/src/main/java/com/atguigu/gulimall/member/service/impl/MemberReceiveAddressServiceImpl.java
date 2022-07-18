package com.atguigu.gulimall.member.service.impl;

import com.atguigu.common.to.UserResponseTo;
import com.atguigu.gulimall.member.interceptor.MyMemberInterceptor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberReceiveAddressDao;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.member.service.MemberReceiveAddressService;
import org.springframework.transaction.annotation.Transactional;


@Service("memberReceiveAddressService")
@Slf4j
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getAddressesByMemberId(Long memberId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<MemberReceiveAddressEntity>().eq(MemberReceiveAddressEntity::getMemberId,memberId));
    }

    @Override
    @Transactional
    public void saveMemberReceiveAddress(MemberReceiveAddressEntity memberReceiveAddress) {
        log.info("saveMember当前线程:{}",Thread.currentThread().getName());
        UserResponseTo userResponseTo = MyMemberInterceptor.loginUser.get();
        if(memberReceiveAddress.getDefaultStatus().equals(1)){
            this.baseMapper.updateAddressStatusById(userResponseTo.getId());
        }
        memberReceiveAddress.setMemberId(userResponseTo.getId());
        this.save(memberReceiveAddress);
    }

    /**
     * 根据主键和会员id删除
     */
    @Override
    public void deleteByIdAndMemberId(Long id) {
        UserResponseTo userResponseTo = MyMemberInterceptor.loginUser.get();
        this.remove(new LambdaQueryWrapper<MemberReceiveAddressEntity>()
                .eq(MemberReceiveAddressEntity::getMemberId,userResponseTo.getId())
                .eq(MemberReceiveAddressEntity::getId,id));
    }


}