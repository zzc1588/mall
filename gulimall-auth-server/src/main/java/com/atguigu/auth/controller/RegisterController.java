package com.atguigu.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.auth.feign.FeignService;
import com.atguigu.auth.vo.UserRegisterVo;
import com.atguigu.common.constant.AuthServiceConstant;
import com.atguigu.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-03 12:12
 **/
@Controller
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private FeignService feignService;
    @RequestMapping("/regist")
    //TODO 用到session就会有分布式问题
    //使用重定向时，model不生效了，可以使用springmvc的RedirectAttributes,重定向视图也能取到值，原理是session，只要跳到下一个页面取到值后会删除session
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            //前置校验
            Map<String, String> errors =
                    result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (oldValue, newValue) -> newValue));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
            // return "reg"; //改该方法直接拼找到并串返回视图，使用转发会有刷新出现表单重复提交问题，最好不要通过转发去到注册页面
            //用户注册是post请求，使用转发会原封不动，还是post请求，但是默认的路径映射器配置类是要求get请求的
//            return "forward:/reg.html";两种方法都可以，这个forward 视图解析器不会进行前后拼串，相当于去到配置的WebMvcConfigurer处理器，处理器在进行转发
        }
        //校验验证码，验证码不存在，验证码错误，验证码过期
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String _code = userRegisterVo.getCode();
        String key = AuthServiceConstant.EMAIL_CODE_REDIS_PREFIX+userRegisterVo.getEmail();
        String value = ops.get(key);

        String code = ops.get(key);
        if(!StringUtils.isEmpty(value)&&_code.equals(code.split("_")[0])){
                //校验成功，调用远程服务member，保存用户信息，跳转登录页面
                R r = feignService.regist(userRegisterVo);
                if(r.getCode() == 0){
                    //保存用户注册信息成功删除redis中的验证码
                    redisTemplate.delete(key);
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    HashMap<String, String> errors = new HashMap<>();
                    String msg = r.get("msg").toString();
                    errors.put("msg",msg);
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
        }else {
            //校验错误跳转注册页面，并返回错误信息
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误或已过期");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }


    }
}
