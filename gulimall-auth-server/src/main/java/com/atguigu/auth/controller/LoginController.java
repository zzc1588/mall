package com.atguigu.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.auth.feign.FeignService;
import com.atguigu.auth.vo.UserInfoVo;
import com.atguigu.auth.vo.UserLoginVo;
import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.constant.ThirdPartyConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.UserResponseTo;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 17:51
 **/
@Controller
@RequestMapping("/auth")
@Validated
@Slf4j
public class LoginController {

    @Autowired
    private FeignService feignService;
    @Autowired
    private StringRedisTemplate restTemplate;

    @GetMapping("/sendEmail")
    @ResponseBody
    public R sendEmailCode(@RequestParam("email") @Email @NotEmpty String email){
        ValueOperations<String, String> ops = restTemplate.opsForValue();
        String redisCode = ops.get(AuthServiceConstant.EMAIL_CODE_REDIS_PREFIX + email);
        if(!StringUtils.isEmpty(redisCode)){
            long oldTime = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis() - oldTime < 60*1000){
                //调用时间小于60s 返回错误
                return R.error(BizCodeEnume.EMAIL_CODE_EXCEPTION.getCode(), BizCodeEnume.EMAIL_CODE_EXCEPTION.getMsg());
            }
        }
        R r0 = feignService.memberAlreadyExist(email);
        if((Boolean) r0.get("isExist")){
            return R.ok().put("msg","该邮箱已注册，请直接登录");
        }
        //验证码，带下划线 _ 后面接时间
        String code = UUID.randomUUID().toString().substring(0, 5)+"_"+System.currentTimeMillis();
        //缓存验证码，下次校验，三分钟有效
        ops.set(AuthServiceConstant.EMAIL_CODE_REDIS_PREFIX+email,code,3, TimeUnit.MINUTES);
        String title = "《谷粒商城注册验证码》";
        R r = feignService.sendEmail(email, title, code.split("_")[0]+"    验证码三分钟内有效");
        if(r.getCode() == 0){
            return R.ok();
        }else {
            return R.error(ThirdPartyConstant.SEND_EMAIL_ERROR.getCode(),ThirdPartyConstant.SEND_EMAIL_ERROR.getMsg());
        }
    }

//    @ResponseBody
    @PostMapping("/common/login")
    public String commonLogin(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){
        R r = feignService.commonLogin(vo);
        if(r.getCode() == 0){
            UserInfoVo userInfoVo = r.getData(new TypeReference<UserInfoVo>() {});
            UserResponseTo userResponseTo = new UserResponseTo();
            BeanUtils.copyProperties(userInfoVo,userResponseTo);
            session.setAttribute(AuthServiceConstant.LOGIN_USER,userResponseTo);
            return "redirect:http://gulimall.com";
        }else {
            HashMap<String, String> errors = new HashMap<>();
            String msg = r.getData("msg",new TypeReference<String>() {});
            errors.put("msg",msg);
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        try {
            session.removeAttribute(AuthServiceConstant.LOGIN_USER);
            session.invalidate();
        }catch (Exception e){
            log.error("退出错误{}",e);
        }
        return "redirect:http://gulimall.com";
    }

}
