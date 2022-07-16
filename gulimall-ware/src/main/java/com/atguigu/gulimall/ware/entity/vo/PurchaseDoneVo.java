package com.atguigu.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-19 18:16
 **/
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单id
    private List<PurchaseItemDoneVo> items;
}
