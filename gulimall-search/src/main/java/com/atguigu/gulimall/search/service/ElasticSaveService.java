package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 19:06
 **/
public interface ElasticSaveService {
    /**
     * 上架商品
     * @param esModels
     * @return
     * @throws IOException
     */
    boolean productStatusUp(List<SkuEsModel> esModels) throws IOException;

    /**
     * 删除商品（下架）
     * @param skuIds
     * @throws IOException
     */
    void soldOutProduct(List<Long> skuIds) throws IOException;
}
