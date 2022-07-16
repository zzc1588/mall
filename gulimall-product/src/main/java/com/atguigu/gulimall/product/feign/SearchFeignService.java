package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-23 19:51
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/search/product")
    R productStatusUp(@RequestBody List<SkuEsModel> esModels);

    @PostMapping("/search/soldOutProduct")
    R soldOutProduct(@RequestParam("skuIds") List<Long> skuIds) throws IOException;
}
