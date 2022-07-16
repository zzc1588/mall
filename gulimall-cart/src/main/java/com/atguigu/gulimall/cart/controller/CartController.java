package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.to.UserInfoTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 23:08
 **/
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/userOrderCartItem")
    @ResponseBody
    public List<CartItem> getUserOrderCartItem(){
        List<CartItem> cartItems = cartService.getUserOrderCartItem();
        System.out.println(cartItems);
        return cartItems;
    }
    @GetMapping("/cart.html")
    public String cartList(Model model) throws ExecutionException, InterruptedException {
        Cart userCart = cartService.getUserCart();
        model.addAttribute("cart",userCart);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId")  Long skuId,
                            @RequestParam("num")  Integer num, RedirectAttributes redirect) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        redirect.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,@RequestParam("num")Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,@RequestParam("checked")Integer checked){
        cartService.checkItem(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/checkItemAll")
    public String checkItemAll(@RequestParam("checked")Integer checked){
        cartService.checkItemAll(checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }



    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/deleteSelectItem")
    public R deleteSelectItem(){
        cartService.deleteSelectItem();
        return R.ok();
    }
}
