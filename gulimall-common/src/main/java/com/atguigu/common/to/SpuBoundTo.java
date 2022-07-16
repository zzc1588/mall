package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-13 0:25
 **/
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
