package com.atguigu.gulimall.member.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-03 18:45
 **/
@Data
public class MemberRegistVo {
    private String userName;
    private String password;
    private String phone;
    private String email;
}
