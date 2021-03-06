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
 * @Author: ?????????
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
        //????????????DSL??????
        SearchResult result = new SearchResult();
        //??????????????????
        SearchRequest searchRequest = builderSearchRequest(param);

        try {
            //??????????????????
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            //???????????????????????????????????????????????????
            result = builderSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //????????????????????????
        return result;
    }

    /**
     * ??????????????????
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

        //??????????????????
        searchResult.setProducts(esModels);
        //???????????????????????????
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

        //???????????????????????????

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
            //????????????
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
            //??????logo
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }

        searchResult.setBrands(brandVos);
        //???????????????????????????
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //??????id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //?????????
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);

            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);
        //???????????? ??????????????????
        searchResult.setPageNum(param.getPageNum());
        long total = hits.getTotalHits().value;
        //?????????
        Integer productPagesize = EsConstant.PRODUCT_PAGESIZE;
        long totalPage = total % productPagesize == 0 ? total / productPagesize : total / productPagesize + 1;
        searchResult.setTotalPage((int)totalPage);
        //???????????????
        searchResult.setTotal(total);
        //??????????????????
        List<Integer> pageNavs = new ArrayList<>();
        for (int i=1;i<=totalPage;i++){
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //?????????????????????
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
                navVo.setNavName("??????");
                navVo.setNavValue(brandHashMap.get(brand));
                String s = "brandId=" + brand;
                extracted(param, s, navVo);
                searchResult.getNavs().add(navVo);
            });
        }
        return searchResult;
    }

    /**
     *  ??????????????????????????????url??????????????????????????????????????????????????????????????????????????????
     * @param param
     * @param value
     * @param navVo
     */
    private void extracted(SearchParam param, String value, SearchResult.NavVo navVo) {
        //2???????????????????????????????????????????????????????????????????????????????????????url?????????????????????
        //??????????????????????????????????????????
        String url = null;
        try {
//                    System.out.println("URL???????????????"+URLDecoder.decode(param.get_queryString(),"UTF-8"));
            url = URLDecoder.decode(param.get_queryString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = url.replace(value, "");
        navVo.setLink("http://search.gulimall.com/list.html?" + replace);
    }


    /**
     * ??????DSL????????????
     * //#?????????????????????????????????????????????????????????????????????????????????
     * //#????????????????????????
     * //#????????????
     * @return
     */
    private SearchRequest builderSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * ?????????????????????????????????????????????????????????????????????????????????
         */
        //??????bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //??????must-????????????
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //??????filter
        //?????????????????????
        if(param.getCatalog3Id()!=null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        //?????????id??????
        if(param.getBrandId()!=null && param.getBrandId().size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //???????????????????????????
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

        //????????????????????????
        if(param.getHasStock() != null ){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }
        //????????????????????? 1_500 _500 1_
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


        //??????????????????????????????query
        sourceBuilder.query(boolQuery);

        /**
         *????????????????????????
         */
        //??????
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }
        //??????
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        //?????? from = ????????????pageNum-1???* size
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        /**
         *????????????
         */
        //TODO ????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //???????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        //TODO ????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);
        //TODO ????????????????????????
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //?????????id????????????
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //?????????id????????????????????? ????????? ?????????????????????
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //?????????id????????????????????? ????????? ?????????????????????
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        //??????
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        System.out.println("?????????DSL?????????"+sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
