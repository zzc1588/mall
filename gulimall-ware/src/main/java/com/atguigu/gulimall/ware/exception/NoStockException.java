package com.atguigu.gulimall.ware.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: 钟质昌
 * @Description: TODO  库存不足异常
 * @DateTime: 2022-06-25 22:12
 **/
public class NoStockException extends RuntimeException{
    @Getter @Setter
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品id:"+skuId+";库存不足");
    }

}
