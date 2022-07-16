package com.atguigu.gulimall.member.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.member.constant.ConstantPropertiesUtil;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberLevelService;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.utils.HttpClientUtils;
import com.atguigu.gulimall.member.entity.vo.UserInfoVo;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-04 14:42
 **/
@CrossOrigin
@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/ucenter/wx")
public class WxApiController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberLevelService memberLevelService;


    @RequestMapping({"/","/member"})
    public String test(){
        return "index";
    }
    /**
     * http://localhost:8160/api/ucenter/wx/callback?code=041QCrll2L5Jh941Aqol2rH26h0QCrlW&state=imhelen
     * @param session
     * @return
     */
    /**
     * @param code
     * @param state
     * @return
     */
    @GetMapping("/callback")
    public String callback(String code, String state, HttpSession session, HttpServletResponse response, RedirectAttributes attributes){
        //得到授权临时票据code
        System.out.println(code);
        System.out.println(state);
        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
        "?appid=%s" +
        "&secret=%s" +
        "&code=%s" +
        "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
//            throw new GuliException(20001, "获取access_token失败");
        }
        //解析json字符串
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String)map.get("access_token");
        String openid = (String)map.get("openid");
        System.out.println("accessToken:"+accessToken);
        System.out.println("openid:"+openid);
        //查询数据库当前用用户是否曾经使用过微信登录
        MemberEntity member = memberService.getByOpenid(openid);
        if(member == null){
            System.out.println("新用户注册");
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
            "?access_token=%s" +
            "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("微信获取的用户详情==========" + resultUserInfo);
            } catch (Exception e) {
//                throw new GuliException(20001, "获取用户信息失败");
            }
//            解析json
            member = new MemberEntity();
            HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
            member.setNickname((String)mapUserInfo.get("nickname"));
            String sex = String.valueOf(mapUserInfo.get("sex")).substring(0,1);
            member.setGender(Integer.parseInt(sex));
            member.setHeader((String)mapUserInfo.get("headimgurl"));
            //默认会员等级
            Long defaultLevelId = memberLevelService.getDefaultLevelId();
            member.setLevelId(defaultLevelId);
            //设置微信登录唯一id
            member.setOpenId((String)mapUserInfo.get("openid"));
//          向数据库中插入一条记录(新用户)
            memberService.save(member);
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(member,userInfoVo);
        userInfoVo.setShowName(member.getNickname());
        session.setAttribute("loginUser", JSON.toJSON(userInfoVo));
        attributes.addFlashAttribute("loginUser", JSON.toJSON(userInfoVo));
        return "redirect:http://gulimall.com";
    }



    @GetMapping("/login")
    public String genQrConnect() {
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
        "?appid=%s" +
        "&redirect_uri=%s" +
        "&response_type=code" +
        "&scope=snsapi_login" +
        "&state=%s" +
        "#wechat_redirect";
        // 回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
//            throw new GuliException(20001, e.getMessage());
        }
        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("state = " + state);
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟
        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,
                state);
        return "redirect:" + qrcodeUrl;
    }
}