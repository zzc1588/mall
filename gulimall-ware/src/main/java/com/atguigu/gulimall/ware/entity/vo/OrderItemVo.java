package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 19:12
 **/
@Data
public class OrderItemVo {
    private Long skuId;

    private Boolean check;

    private String title;

    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;

    /** 商品重量 **/
    private BigDecimal weight = new BigDecimal("0.085");
}
