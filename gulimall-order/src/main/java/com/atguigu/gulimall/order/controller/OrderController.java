package com.atguigu.gulimall.order.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.rabbitmq.client.Delivery;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 订单
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:43:51
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 判断订单是否存在
     * @param orderSn
     * @return
     */
    @GetMapping("/{orderSn}/getByOrderSn")
    public R getByOrderSn(@PathVariable("orderSn") String orderSn){
        Long count = orderService.getByOrderSn(orderSn);
        return R.ok().put("hasOrder",count > 0);
    }


    /**
     * 取消订单
     * @param orderSn
     * @param status
     * @return
     */
    @PostMapping("/cancelOrder")
    public R cancelOrder(@RequestParam("orderSn") String orderSn,@RequestParam("status") Integer status){
        Long count = orderService.cancelOrder(orderSn,status);
        return R.ok();
    }

    /**
     * 获取订单详情
     * @param orderSn
     * @return
     */
    @GetMapping("/{orderSn}/getOrderInfoByOrderSn")
    public R getOrderInfoByOrderSn(@PathVariable("orderSn") String orderSn){
        OrderEntity order  = orderService.getOrderInfoByOrderSn(orderSn);
        return R.ok().setData(order);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);
        return R.ok().put("page", page);
    }



    /**
     * 用户订单列表和订单详情
     */
    @PostMapping("/listOrderWithItem")
    public R listOrderWithItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.listOrderWithItem(params);
        int nav = page.getTotalPage() / page.getPageSize() + page.getTotalPage() % page.getPageSize();
        List<Integer> pageList = new ArrayList<>();
        for (int i=0;i<nav;i++){
            pageList.add(i);
        }
        return R.ok().put("page", page).put("pageList",pageList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
