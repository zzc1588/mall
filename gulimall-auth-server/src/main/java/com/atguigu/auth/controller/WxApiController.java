package com.atguigu.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.auth.feign.FeignService;
import com.atguigu.auth.utils.ConstantWxUtils;
import com.atguigu.auth.utils.HttpClientUtils;
import com.atguigu.auth.vo.UserInfoVo;
import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.to.UserResponseTo;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-08-10 20:28
 **/

@Slf4j
@Controller
@RequestMapping(value = "/api/ucenter/wx")
public class WxApiController {

    @Autowired
    FeignService feignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "weChar_open:state:";

    /**
     * 获取扫码人的信息，添加数据
     * @return
     */
    @GetMapping(value = "/callback")
    public String callback(String code, String state, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("微信回调本地接口");
        //从redis中将state获取出来，和当前传入的state作比较
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if(ops.get(KEY_PREFIX + state) == null){
            log.info("非法访问微信回调接口");
            return "redirect:http://auth.gulimall.com/login.html";
        }
        //如果一致则放行，如果不一致则抛出异常：非法访问

        //向认证服务器发送请求换取access_token
        //2、拿着code请求 微信固定的地址，得到两个 access_token 和 openid
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        //拼接三个参数：id 秘钥 和 code 值
        String accessTokenUrl = String.format(
                baseAccessTokenUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_APP_SECRET,
                code
        );

        String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
        R r = feignService.weixinLogin(accessTokenInfo);
        if (r.getCode() == 0) {
            UserInfoVo data = r.getData("data", new TypeReference<UserInfoVo>() {});
            data.setShowName(data.getNickname());
            log.info("登录成功：用户信息：{}",data);

            //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
            //以后浏览器访问哪个网站就会带上这个网站的cookie
            //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
            UserResponseTo userResponseTo = new UserResponseTo();
            BeanUtils.copyProperties(data,userResponseTo);
            session.setAttribute(AuthServiceConstant.LOGIN_USER,userResponseTo);
            // 使用基本编码
            String base64encodedString = Base64.getEncoder().encodeToString(session.getId().getBytes("utf-8"));
            log.info("Base64 编码字符串 (基本) :" + base64encodedString);
            //2、登录成功跳回首页
            return "redirect:http://gulimall.com?WXACCESS="+base64encodedString;
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     * 生成微信扫描二维码
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping(value = "/login")
    public String getWxCode() throws UnsupportedEncodingException {
        //微信开发平台授权baseUrl   %s相当于？表示占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //对redirect_url进行URLEncoder编码
        String redirect_url = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        redirect_url = URLEncoder.encode(redirect_url,"UTF-8");

        // 防止csrf攻击（跨站请求伪造攻击）
        String state = UUID.randomUUID().toString().replace("-", "");//一般情况下会使用一个随机数
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(KEY_PREFIX+state,"",30, TimeUnit.MINUTES);
//        String state = "https://d2d8-218-14-90-130.jp.ngrok.io";//为了能够使用我搭建的外网的微信回调跳转服务器，这里填写在ngrok的前置域名
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键： "wechar-open-state-" + httpServletRequest.getSession().getId()
        //值： satte
        //过期时间： 30分钟
        //生成qrcodeUrl
        //设置%s中的值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirect_url,
                state
        );

        //重定向到请求微信地址
        return "redirect:" + url;
    }

}
