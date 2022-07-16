package com.atguigu.gulimall.product.entity.vo.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-24 12:05
 * 二级分类的vo
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {

    private String catalog1Id;//1级父分类
    private List<Catelog2Vo.Catelog3Vo> catalog3List;//三级分类
    private String id;
    private String name;

    /**
     * 三级分类的vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
