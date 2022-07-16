package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.UserInfoTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.CartGatewayFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 23:07
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CartGatewayFeignService feignService;
    @Autowired
    private ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        /**
         添加到购物车步骤：
         1.判断用户是否登录
            直接判断userid 是否为空
         2.判断该用户的购物车中是否存在这件商品
            存在则操作为 oldNum+newNum，不存在操作为+newNum
         */

        CartItem cartItem = getCartItem(skuId);
        BoundHashOperations<String, Object, Object> cart = getCart();
        if(cartItem!=null){
            //已存在该商品
            cartItem.setCount(cartItem.getCount()+num);
            cart.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }else {
            CartItem item = new CartItem();
            CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
                R info = feignService.info(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                item.setSkuId(skuId);
                item.setImage(skuInfo.getSkuDefaultImg());
                item.setTitle(skuInfo.getSkuTitle());
                item.setPrice(skuInfo.getPrice());
                item.setCount(num);
            }, executor);

            CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
                try {
                    R r = feignService.stringList(skuId);
                    List<String> listValue = r.getData("listValue", new TypeReference<List<String>>() {
                    });
                    item.setSkuAttr(listValue);
                } catch (Exception e) {

                }
            }, executor);
            CompletableFuture.allOf(task1,task2).get();
            cart.put(skuId.toString(),JSON.toJSONString(item));
            return item;
        }
    }
    @Override
    public List<CartItem> batchAddToCart(List<CartItem> cartItems){
        cartItems.stream().forEach(item->{
            try {
                addToCart(item.getSkuId(),item.getCount());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        BoundHashOperations<String, Object, Object> operations = getCart();
        List<Object> values = operations.values();
        return cartItems;
    }
    /**
     * 获取购物车redis操作
     * @return
     */
    @Override
    public BoundHashOperations<String, Object, Object> getCart() {

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId()!=null){
           //已登录
            String loginHashKey = CART_PREFIX + userInfoTo.getUserId().toString();
            //登录以后需要把临时用户的的购物车信息合并
            BoundHashOperations<String, Object, Object> loginCart
                    = redisTemplate.boundHashOps(loginHashKey);
            return loginCart;

        }else {
            //未登录
            String tempHashKey = CART_PREFIX + userInfoTo.getUserKey();;
            BoundHashOperations<String, Object, Object> tempCart
                    = redisTemplate.boundHashOps(tempHashKey);
            return tempCart;
        }


    }
    @Override
    public Cart getUserCart(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        String loginHashKey = CART_PREFIX + userInfoTo.getUserId();
        String tempHashKey = CART_PREFIX + userInfoTo.getUserKey();
        List<CartItem> tempCartItemList = getCartItemList(tempHashKey);
        List<CartItem> loginCartItemList = getCartItemList(loginHashKey);
        if (userInfoTo.getUserId() != null){
            //已登录
            //登录以后需要把临时用户的的购物车信息合并
            if(tempCartItemList !=null && tempCartItemList.size()>0){
                tempCartItemList.forEach(item->{
                    try {
                        addToCart(item.getSkuId(), item.getCount());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                List<CartItem> cartItemList = getCartItemList(loginHashKey);
                cart.setItems(cartItemList);
                //合并后清空临时购物车
                clearCart(tempHashKey);
            }else if(loginCartItemList != null && loginCartItemList.size()>0){
                cart.setItems(loginCartItemList);
            }
        }else {
            //未登录
            if(tempCartItemList!=null && tempCartItemList.size()>0){
                cart.setItems(tempCartItemList);
            }

        }
        return cart;
    }

    /**
     * 获取购物车项
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cart = getCart();
        String str =(String) cart.get(skuId.toString());
        if(str != null){
            return JSON.parseObject(str, CartItem.class);
        }
        return null;
    }

    @Override
    public List<CartItem> getCartItemList(String cartKey) {
        BoundHashOperations<String, Object, Object> cart
                = redisTemplate.boundHashOps(cartKey);
        List<Object> values = cart.values();
        if(cart != null && cart.size()>0){
            List<CartItem> itemList = values.stream().map(v -> {
                String str = (String) v;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return itemList;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> operations = getCart();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        operations.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> operations = getCart();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(checked == 1 ? true : false);
        operations.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> operations = getCart();
        operations.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserOrderCartItem() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        List<CartItem> cartItemList =
                getCartItemList(CART_PREFIX + userInfoTo.getUserId());
        if(cartItemList!=null && cartItemList.size()>0){
            List<CartItem> collect =
                    cartItemList.stream()
                            .filter((item) -> item.getCheck())
                            .map(item->{
                                //获取最新价格
                                String newSkuPriceById = feignService.getNewSkuPriceById(item.getSkuId());
                                item.setPrice(new BigDecimal(newSkuPriceById));
                                return item;
                            })
                            .collect(Collectors.toList());
            return collect;
        }
        return null;

    }

    /**
     * 删除购物车中已购买的商品
     * @return
     */
    @Override
    public void deleteSelectItem() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> operations = getCart();
        List<CartItem> itemList = getUserOrderCartItem();
        itemList.stream().forEach(item->{
                operations.delete(CART_PREFIX + userInfoTo.getUserId().toString(), item.getSkuId().toString());
        });
    }

    @Override
    public void checkItemAll(Integer checked) {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> operations = getCart();
        String cartKey ;
        if(userInfoTo.getUserId()!=null){
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        List<CartItem> cartItemList = getCartItemList(cartKey);
        if(cartItemList!=null && cartItemList.size()>0){
            cartItemList.stream().forEach(item -> {
                item.setCheck(checked == 1 ? true : false);
                operations.put(item.getSkuId().toString(),JSON.toJSONString(item));
            });
        }


    }

}
