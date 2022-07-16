package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 1:10
 **/
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
