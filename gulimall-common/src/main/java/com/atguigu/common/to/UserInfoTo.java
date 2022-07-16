package com.atguigu.common.to;

import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 23:15
 **/
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private Boolean isTemp =false;
}
