package com.atguigu.gulimall.member.service;

import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.gulimall.member.entity.vo.MemberRegistVo;
import com.atguigu.gulimall.member.entity.vo.SocialUser;
import com.atguigu.gulimall.member.entity.vo.UserLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveMemberRegist(MemberRegistVo vo);

    MemberEntity getByOpenid(String openid);

    MemberEntity getByUserName(String  userName);

    MemberEntity commonLogin(UserLoginVo vo);

    MemberEntity login(String accessTokenInfo);

    /**
     * 社交用户的登录
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser) throws Exception;


    /**
     * 查询用户是否存在
     * @param email
     * @return
     */
    Long memberAlreadyExist(String email);

    /**
     * 修改用户积分，成长值
     */
    void updateIntegrationAndGrowth(OrderEntityTo to);
}

