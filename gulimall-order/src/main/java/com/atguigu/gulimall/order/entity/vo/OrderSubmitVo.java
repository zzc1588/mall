package com.atguigu.gulimall.order.entity.vo;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 16:37
 **/
@Data
public class OrderSubmitVo {
    @NotNull
    private  Long addrId;//收获地址id
    @NotNull @Min(0) @Max(4)
    private  Integer payType;//支付方式
    @NotBlank
    private  String orderToken; // 订单验证令牌
    @NotNull @Digits(integer = 12,fraction = 4)
    private  BigDecimal payPrice; //购物车中的应付价格；
}
