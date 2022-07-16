package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.FeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-26 18:02
 **/
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private FeignService feignService;

    @Override
    public SearchResult search(SearchParam param) {
        //动态构建DSL语句
        SearchResult result = new SearchResult();
        //准备检索请求
        SearchRequest searchRequest = builderSearchRequest(param);

        try {
            //执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            //分析响应数据，封装成我们需要的格式
            result = builderSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //构建聚合分析语句
        return result;
    }

    /**
     * 构建响应结果
     * @param response
     * @return
     */
    private SearchResult builderSearchResult(SearchResponse response,SearchParam param) {
        SearchResult searchResult = new SearchResult();
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits.getHits() !=null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(!StringUtils.isEmpty(param.getKeyword())){
                    String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].toString();
                    skuEsModel.setSkuTitle(skuTitle);
                }
                esModels.add(skuEsModel);
            }
        }

        //返回所有商品
        searchResult.setProducts(esModels);
        //所有商品涉及的属性
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
//            for (Terms.Bucket attr_value_aggBucket : attr_value_agg.getBuckets()) {
//                attrValues.add(attr_value_aggBucket.getKeyAsString());
//            }
            List<String> attrValues =
                    attr_value_agg.getBuckets()
                            .stream()
                            .map(item -> item.getKeyAsString())
                            .collect(Collectors.toList());
            attrVo.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);

        //所有商品涉及的品牌

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
            //品牌名称
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
            //品牌logo
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }

        searchResult.setBrands(brandVos);
        //所有商品涉及的分类
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);

            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);
        //分页信息 页码，总页数
        searchResult.setPageNum(param.getPageNum());
        long total = hits.getTotalHits().value;
        //总页数
        Integer productPagesize = EsConstant.PRODUCT_PAGESIZE;
        long totalPage = total % productPagesize == 0 ? total / productPagesize : total / productPagesize + 1;
        searchResult.setTotalPage((int)totalPage);
        //总数据条数
        searchResult.setTotal(total);
        //设置页码导航
        List<Integer> pageNavs = new ArrayList<>();
        for (int i=1;i<=totalPage;i++){
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //设置面包屑导航
        if(param.getAttrs()!=null && param.getAttrs().size() >0){
            HashMap<Long, String> attrHashMap = new HashMap<>();
            for (SearchResult.AttrVo attr : searchResult.getAttrs()) {
                attrHashMap.put(attr.getAttrId(),attr.getAttrName());
            }
            param.getAttrs().stream().forEach(attr -> {
                System.out.println("strea:"+attr);
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavName(attrHashMap.get(Long.parseLong(s[0])));
                navVo.setNavValue(s[1]);
                String s1 = "attrs=" + attr;
                extracted(param, s1, navVo);
                searchResult.getNavs().add(navVo);
            });
        }
        if(param.getBrandId()!=null && param.getBrandId().size() > 0){
            HashMap<Long, String> brandHashMap = new HashMap<>();
            for (SearchResult.BrandVo brandVo : searchResult.getBrands()) {
                brandHashMap.put(brandVo.getBrandId(),brandVo.getBrandName());
            }
            param.getBrandId().stream().forEach(brand -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavName("品牌");
                navVo.setNavValue(brandHashMap.get(brand));
                String s = "brandId=" + brand;
                extracted(param, s, navVo);
                searchResult.getNavs().add(navVo);
            });
        }
        return searchResult;
    }

    /**
     *  设置面包屑导航标签的url地址，除去自己的参数，包含其他参数即可实现面包屑导航
     * @param param
     * @param value
     * @param navVo
     */
    private void extracted(SearchParam param, String value, SearchResult.NavVo navVo) {
        //2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
        //拿到所有的查询条件，去掉当前
        String url = null;
        try {
//                    System.out.println("URL地址解码："+URLDecoder.decode(param.get_queryString(),"UTF-8"));
            url = URLDecoder.decode(param.get_queryString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = url.replace(value, "");
        navVo.setLink("http://search.gulimall.com/list.html?" + replace);
    }


    /**
     * 构建DSL检索请求
     * //#模糊匹配。过滤（属性，分类，品牌，价格区间，库存状态）
     * //#排序。高亮。分页
     * //#聚合分析
     * @return
     */
    private SearchRequest builderSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配。过滤（属性，分类，品牌，价格区间，库存状态）
         */
        //构建bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //构建must-模糊匹配
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //构建filter
        //按三级分类过滤
        if(param.getCatalog3Id()!=null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        //按品牌id过滤
        if(param.getBrandId()!=null && param.getBrandId().size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //按所有指定属性过滤
        if(param.getAttrs()!=null && param.getAttrs().size() > 0){
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }

        //按库存是否有过滤
        if(param.getHasStock() != null ){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }
        //按价格区间过滤 1_500 _500 1_
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if(s.length == 2){
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }else if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }


        //把上面构建的拿来封装query
        sourceBuilder.query(boolQuery);

        /**
         *排序。高亮。分页
         */
        //排序
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }
        //高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        //分页 from = 当前页（pageNum-1）* size
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        /**
         *聚合分析
         */
        //TODO 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //品牌子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        //TODO 分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);
        //TODO 嵌套类型属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //对属性id聚合分析
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //对属性id聚合分析的结果 再进行 属性名聚合分析
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //对属性id聚合分析的结果 再进行 属性值聚合分析
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        //装载
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        System.out.println("构建的DSL语句："+sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
