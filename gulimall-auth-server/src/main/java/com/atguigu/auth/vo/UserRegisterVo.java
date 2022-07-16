package com.atguigu.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-03 0:12
 **/
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6,max = 18,message = "用户名必须是6-18位字符")
    private String userName;

    @NotEmpty(message = "密码必须填写")
    private String password;

    @NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机格式不正确")
    private String phone;

    @NotEmpty(message = "邮箱必须填写")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotEmpty(message = "验证码必须填写")
    private String code;
}
