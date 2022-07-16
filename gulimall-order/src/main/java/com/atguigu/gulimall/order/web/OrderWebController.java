package com.atguigu.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.alipay.AlipayTemplate;
import com.atguigu.gulimall.order.entity.vo.PaySyncVo;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.entity.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.entity.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.entity.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 12:53
 **/
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("/toTrade")
    public String toTrade(Model model, RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.orderConfirmData();
        model.addAttribute("confirmOrderData",orderConfirmVo);
        if (orderConfirmVo.getItems() !=null && orderConfirmVo.getItems().size()>0){
            return "confirm";
        }else {
            attributes.addAttribute("msg","请至少选中一件商品");
            return "redirect:http://cart.gulimall.com/cart.html";
        }
    }

    @PostMapping("/submitOrder")
    public String submitOrder(@Validated OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes){
        SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);
        if(responseVo.getCode() == 0){
            //下单成功去支付页面
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            //下单失败去订单确认页
            String msg = "下单失败：";
            switch (responseVo.getCode()){
                case 1: msg+="订单失效请刷新重试";break;
                case 2: msg+="商品价格发生变化请确认后提交";break;
            }
            Map<String, String> map = new HashMap();
            map.put("msg",msg);
            redirectAttributes.addFlashAttribute("map",map);
            return "redirect:http://cart.gulimall.com/cart.html";
        }
    }

    /**
     * 为了简化集成流程，商家可以将同步结果仅作为一个支付结束的通知（忽略执行校验），实际支付是否成功，完全依赖服务端异步通知。
     * @param vo
     * @param request
     * @return
     * @throws AlipayApiException
     */
    @RequestMapping("/aliPayReturnUrl")
    public String aliPayReturnUrl(PaySyncVo vo, HttpServletRequest request) throws AlipayApiException {
        boolean signVerified = isSignVerified(request);
        if (signVerified){
            System.out.println("支付宝同步回调验签正确");
        }else {

        }
        return "redirect:http://member.gulimall.com/memberOrder.html";
    }


    private boolean isSignVerified(HttpServletRequest request) throws AlipayApiException {
        //TODO 需要验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        return signVerified;
    }
}
