package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.data.redis.core.BoundHashOperations;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 23:07
 **/
public interface CartService {
    /**
     * 根据登录状态  以及 购物车中是否存在该商品 来添加商品
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 废弃方法
     * @param cartItems
     * @return
     */
    @Deprecated
    List<CartItem> batchAddToCart(List<CartItem> cartItems);

    /**
     * 获取操作购物车cart的的redis操作实例
     * @return
     */
    BoundHashOperations<String, Object, Object> getCart();

    /**
     * ，根据登录状态，获取用户的购物车
     * @return
     */
    Cart getUserCart();

    /**
     * 根据id 获取商品
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取临时\登录 状态下购物车中的所有商品
     * @param cartKey
     * @return
     */
    List<CartItem> getCartItemList(String cartKey) ;

    /**
     * 清空购物车
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 修改商品数量+ - (不判断该商品是否存在，直接插入数量为 num)
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    /**
     * 商品选中状态
     * @param skuId
     * @param checked
     */
    void checkItem(Long skuId, Integer checked);

    /**
     * 删除商品
     * @param skuId
     */
    void deleteItem(Long skuId);

    /**
     * 订单模块，获取用户当前购物车中已选中的购物项
     * @return
     */
    List<CartItem> getUserOrderCartItem();

    /**
     * 订单创建成功，删除用户购物车中选中的商品
     * @return
     */
    void deleteSelectItem();

    /**
     * 选中、取消选中所有商品
     * @param checked
     */
    void checkItemAll(Integer checked);
}
