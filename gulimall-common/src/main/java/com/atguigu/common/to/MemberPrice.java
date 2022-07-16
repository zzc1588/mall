package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-18 19:23
 **/
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}