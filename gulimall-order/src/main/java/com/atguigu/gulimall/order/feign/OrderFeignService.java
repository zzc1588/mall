package com.atguigu.gulimall.order.feign;

import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.entity.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.entity.vo.OrderItemVo;
import com.atguigu.gulimall.order.entity.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 15:17
 **/
@FeignClient("gulimall-gateway")
public interface OrderFeignService {
    @GetMapping("/api/member/member/{memberId}/addresses")
    List<OrderConfirmVo.MemberAddressVos> getAddressesByMemberId(@PathVariable("memberId") Long memberId);

    /**
     * 查询当前用户购物车选中的商品项
     * @return
     */
    @GetMapping("/api/cart/userOrderCartItem")
    List<OrderItemVo> getUserOrderCartItem();

    /**
     * 检查是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/api/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/api/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId")Long addrId);

    /**
     * 根据skuId 获取spuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/spuinfo/spuInfo/{skuId}")
    R getSpuBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping("/api/ware/waresku/lock/order")
    R LockOrderStock(@RequestBody WareSkuLockVo vo);

    @GetMapping("/api/cart/deleteSelectItem")
    R deleteSelectItem();

    @PostMapping("/api/rabbitMq/sendQueueMessage")
    R sendQueueMessage(@RequestBody QueueMessageTo queueMessageTo);

    @PostMapping("/api/rabbitMq/consumeMessageFail")
    R consumeMessageFail(@RequestParam("messageId") Long messageId);
}
