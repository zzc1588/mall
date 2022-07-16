package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-19 16:50
 **/
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
