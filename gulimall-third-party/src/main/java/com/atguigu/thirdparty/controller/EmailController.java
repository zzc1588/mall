package com.atguigu.thirdparty.controller;

import com.atguigu.common.utils.R;
import com.atguigu.thirdparty.constant.ThirdPartyConstant;
import com.atguigu.thirdparty.utils.SendEmailUtil;
import org.apache.commons.mail.EmailConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.util.UUID;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 20:53
 **/
@RestController
@RequestMapping("/email")
public class EmailController {

    @GetMapping
    public R sendEmail(@RequestParam("email") String email,@RequestParam("title") String title,@RequestParam("code") String code){
        try {
            SendEmailUtil.sendMail("786614275@qq.com","ggigmnvxkxzqbaic",email,title,code);
        } catch (MessagingException e) {
            e.printStackTrace();
            return R.error(ThirdPartyConstant.SEND_EMAIL_ERROR.getCode(), ThirdPartyConstant.SEND_EMAIL_ERROR.getMsg());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return R.error(ThirdPartyConstant.SEND_EMAIL_ERROR.getCode(), ThirdPartyConstant.SEND_EMAIL_ERROR.getMsg());
        }
        return R.ok();
    }
}
