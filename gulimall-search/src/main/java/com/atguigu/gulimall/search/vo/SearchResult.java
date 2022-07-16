package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-26 18:19
 **/
@Data
public class SearchResult {
    //查询到的商品信息
    private List<SkuEsModel> products;
    //分页信息，因为数据是es中获取，所以mybatis-plus分页插件无用
    private Integer pageNum;//当前页码
    private Long total;//总记录
    private Integer totalPage;//总页码

    private List<BrandVo> brands;//当前查询到的所有结果，所有结果涉及的品牌
    private List<CatalogVo> catalogs;//当前查询到的所有结果，所有结果涉及的所有分类
    private List<AttrVo> attrs;//当前查询到的所有结果，所有结果涉及的所有属性
    private List<Integer> pageNavs;

    //面包屑导航
    private List<NavVo> navs = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;

    }


    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
