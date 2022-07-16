package com.atguigu.common.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-26 18:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareOrderTaskDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
