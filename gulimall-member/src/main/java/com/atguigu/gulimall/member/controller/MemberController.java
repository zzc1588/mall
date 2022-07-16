package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.RRException;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberReceiveAddressService;
import com.atguigu.gulimall.member.entity.vo.MemberRegistVo;
import com.atguigu.gulimall.member.entity.vo.SocialUser;
import com.atguigu.gulimall.member.entity.vo.UserInfoVo;
import com.atguigu.gulimall.member.entity.vo.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 会员
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;


    @GetMapping("/memberAlreadyExist")
    public R memberAlreadyExist(@RequestParam("email") String email){
        Long count = memberService.memberAlreadyExist(email);
        if(count == 0){
            return R.ok().put("isExist",false);
        }else {
            return R.ok().put("isExist",true);
        }
    }


    @GetMapping("/{memberId}/addresses")
    public List<MemberReceiveAddressEntity> getAddressesByMemberId(@PathVariable("memberId") Long memberId){
        List<MemberReceiveAddressEntity> addressList = memberReceiveAddressService.getAddressesByMemberId(memberId);
        return addressList;
    }

    @PostMapping(value = "/weixin/login")
    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {

        MemberEntity memberEntity = memberService.login(accessTokenInfo);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.REGISTER_EMAIL_UNIQUE.getMsg());
        }
    }

    @RequestMapping("/coupons")
    public R getMemberCoupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R r = couponFeignService.testOpenFeign();

        return R.ok().put("member",memberEntity).put("coupons",r.get("coupons"));
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 用户信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo)  {
        try {
            memberService.saveMemberRegist(vo);
        }catch (RRException exception){
            if(exception.getCode() == BizCodeEnume.REGISTER_USERNAME_UNIQUE.getCode()){
                return R.error(BizCodeEnume.REGISTER_USERNAME_UNIQUE.getCode(),BizCodeEnume.REGISTER_USERNAME_UNIQUE.getMsg());
            }
            if(exception.getCode() == BizCodeEnume.REGISTER_PHONE_UNIQUE.getCode()){
                return R.error(BizCodeEnume.REGISTER_PHONE_UNIQUE.getCode(),BizCodeEnume.REGISTER_PHONE_UNIQUE.getMsg());
            }
            if(exception.getCode() == BizCodeEnume.REGISTER_EMAIL_UNIQUE.getCode()){
                return R.error(BizCodeEnume.REGISTER_EMAIL_UNIQUE.getCode(),BizCodeEnume.REGISTER_EMAIL_UNIQUE.getMsg());
            }
        }
        return R.ok();
    }

    @PostMapping("/common/login")
        public R commonLogin(@RequestBody UserLoginVo vo){
        MemberEntity memberEntity = memberService.commonLogin(vo);
        if(memberEntity != null){
            UserInfoVo userInfoVo = new UserInfoVo();
            userInfoVo.setId(memberEntity.getId());
            BeanUtils.copyProperties(memberEntity,userInfoVo);
            userInfoVo.setShowName(vo.getUserName());
            return R.ok().setData(userInfoVo);
        }else {
            return R.error(BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.getMsg());
        }
    }


    @PostMapping(value = "/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {

        MemberEntity memberEntity = memberService.login(socialUser);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGIN_PASSWORD_EXCEPTION.getMsg());
        }
    }
}
