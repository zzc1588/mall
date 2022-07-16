package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ElasticSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 19:08
 **/
@Service
@Slf4j
public class ElasticSaveServiceImpl implements ElasticSaveService {
    @Autowired
    private RestHighLevelClient client;
    @Override

    public boolean productStatusUp(List<SkuEsModel> esModels) throws IOException {
        //保存到es
        //给es建立索引和映射关系
        //给es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : esModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(esModel.getSkuId()));
            String jsonString = JSON.toJSONString(esModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        //TODO 批量保存错误处理
        boolean b = bulk.isFragment();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架成功：{},返回数据：{}",collect,bulk.toString());
        return b;
    }

    @Override
    public void soldOutProduct(List<Long> skuIds) throws IOException {
        for (Long skuId : skuIds) {
            DeleteRequest deleteRequest = new DeleteRequest(EsConstant.PRODUCT_INDEX);
            deleteRequest.id(String.valueOf(skuId));
            client.delete(deleteRequest,GulimallElasticSearchConfig.COMMON_OPTIONS);
        }
    }
}
