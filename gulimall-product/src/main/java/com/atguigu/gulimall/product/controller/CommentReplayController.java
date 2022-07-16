package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CommentReplayEntity;
import com.atguigu.gulimall.product.service.CommentReplayService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品评价回复关系
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@RestController
@RequestMapping("product/commentreplay")
@Api(tags ="商品评价回复关系Controller")

public class CommentReplayController {
    @Autowired
    private CommentReplayService commentReplayService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = commentReplayService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CommentReplayEntity commentReplay = commentReplayService.getById(id);

        return R.ok().put("commentReplay", commentReplay);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CommentReplayEntity commentReplay){
		commentReplayService.save(commentReplay);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody CommentReplayEntity commentReplay){
		commentReplayService.updateById(commentReplay);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		commentReplayService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
