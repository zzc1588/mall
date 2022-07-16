package com.atguigu.auth.feign;

import com.atguigu.auth.vo.UserLoginVo;
import com.atguigu.auth.vo.UserRegisterVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 21:07
 **/
@FeignClient("gulimall-gateway")
public interface FeignService {
    @GetMapping("/api/thirdparty/email")
    R sendEmail(@RequestParam("email") String email,
                       @RequestParam("title") String title,
                       @RequestParam("code") String code);


    @PostMapping("/api/member/member/regist")
    R regist(@RequestBody  UserRegisterVo vo);

    @PostMapping("/api/member/member/common/login")
    R commonLogin(@RequestBody UserLoginVo vo);

    @PostMapping(value = "/api/member/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);


    @GetMapping("/api/member/member/memberAlreadyExist")
    R memberAlreadyExist(@RequestParam("email") String email);
}
