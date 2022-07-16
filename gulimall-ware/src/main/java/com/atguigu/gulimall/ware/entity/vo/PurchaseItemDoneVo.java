package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-19 18:16
 **/
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
