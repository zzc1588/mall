package com.atguigu.common.to;

import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 17:25
 **/
@Data
public class SkuHasStockVo {
    private Long SkuId;
    private Boolean hasStock;
}
