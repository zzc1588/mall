package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 18:54
 **/
@Data
public class LockSkuResult {
    private Long skuId;//锁定的商品
    private Integer num;//锁几件
    private Boolean locked;//锁定成功\失败
}
