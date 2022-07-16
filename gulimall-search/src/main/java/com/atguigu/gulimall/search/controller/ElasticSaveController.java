package com.atguigu.gulimall.search.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.feign.FeignService;
import com.atguigu.gulimall.search.service.ElasticSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 18:51
 **/
@Slf4j
@RestController
@RequestMapping("/search")
public class ElasticSaveController {
    @Autowired
    private ElasticSaveService elasticSaveService;

    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> esModels){
        boolean b = false;
        try {
            b = elasticSaveService.productStatusUp(esModels);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ElasticSaveController商品上架错误：{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode()
                    ,BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(!b){
            return R.ok();
        }else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode()
                    ,BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/soldOutProduct")
    public R soldOutProduct(@RequestParam("skuIds") List<Long> skuIds) throws IOException {
        elasticSaveService.soldOutProduct(skuIds);
        return R.ok();
    }
}
