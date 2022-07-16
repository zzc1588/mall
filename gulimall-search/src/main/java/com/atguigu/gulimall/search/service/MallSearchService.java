package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-26 18:02
 **/
public interface MallSearchService {
    /**
     *
     * @param param 检索条件
     * @return 检索结果
     */
    SearchResult search(SearchParam param);
}
