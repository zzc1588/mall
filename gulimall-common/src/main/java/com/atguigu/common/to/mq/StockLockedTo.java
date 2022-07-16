package com.atguigu.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-26 18:05
 **/
@Data
public class StockLockedTo {
    private Long id;
    private List<WareOrderTaskDetailTo> taskDetailList;
}
