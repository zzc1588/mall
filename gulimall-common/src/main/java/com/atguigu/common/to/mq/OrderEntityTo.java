package com.atguigu.common.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-07 21:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderEntityTo {
    private Long memberId;
    /**
     * 可以获得的积分
     */
    private Integer integration;
    /**
     * 可以获得的成长值
     */
    private Integer growth;
}
