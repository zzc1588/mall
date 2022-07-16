package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-26 17:30
 **/
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 让springMvc自动将页面提交的参数封装成指定对象
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request){
        /**
         * g根据传过来的参数去es中查询
         */
        String queryString = request.getQueryString();
        param.set_queryString(queryString);
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }


}
