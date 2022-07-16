package com.atguigu.auth.vo;

import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-11 19:43
 **/
@Data
public class UserInfoVo {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private Integer gender;
    private String showName;

}
