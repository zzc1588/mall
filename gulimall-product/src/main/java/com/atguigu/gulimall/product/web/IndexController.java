package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.entity.vo.web.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-24 11:41
 **/
@Controller
@Slf4j
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //默认视图解析器进行拼串 前缀resources/templates，后缀都有.html
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Catetorys();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String,List<Catelog2Vo>> getCatelogJson(){
        Map<String,List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @GetMapping("/product/testRedisson")
    @ResponseBody
    public String testRedisson(){
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功！！！");
            Thread.sleep(3000);
        }catch (Exception e){

        }finally {
            System.out.println("释放锁");
            lock.unlock();
        }
        return "ok";
    }

}
