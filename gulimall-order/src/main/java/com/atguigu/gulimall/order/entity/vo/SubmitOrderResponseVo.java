package com.atguigu.gulimall.order.entity.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-25 16:32
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;  //状态码 0-成功

}
