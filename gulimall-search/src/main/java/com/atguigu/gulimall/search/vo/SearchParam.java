package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-26 17:59
 **/
@Data
public class SearchParam {
    //关键字
    private String keyword;
    //分类id
    private Long catalog3Id;
    //品牌id (多选)
    private List<Long> brandId;
    //排序
    private String sort;
    //是否有货
    private Integer hasStock;
    //按照价格区间筛选
    private String skuPrice;
    //按照属性筛选
    private List<String> attrs;
    //页码
    private Integer pageNum = 1;
    //url地址的查询参数
    private String _queryString;

}
