package com.atguigu.gulimall.order.config.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
@Slf4j
public class AlipayTemplate {
    //在支付宝创建的应用的id
    private   String app_id = "2016093000632190";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCwEdrOSGjDQPYD7SF6tYEiYR7TCSZk8R6ysqXUlj2uUbolSScPo/T2mxxkva8qjPqWD2xS5yJx8CbPK6r06tdCFsEU19PTJjk+elmWtrsTRjlyCNfki3Q/lbSy7OZItQVIwXQVqXd3UB7VUtnVMCL1nQvO/XCz+14YH2BOA4P0S2tv/PXgwK0RMzdHQUXabFSSsgg2Go5Ufnlt/PIZHX8QPf8tNtTzdaSfqaMuUwfDufwfdOsGaShYW+fXXY4ohkOjc6FCudUzgDYYVagQ9PJzHHAHPWbf7iIJFuGvdAz7yVfSj2yZjJYSugjqUTZyyIjyot+Ny/cMuX7+eRKiMPuXAgMBAAECggEAQudV9IKxRFh/4zgNO7qSikTLWLemXj79Qjv+JYy9fWrSx+5HKHtNzxNbs7AcSpftdG4B5HKttQvjQ1+9g3llOFi7H7dvZ1Gj+Oi2+D0RUx6hH4Lavp57GtpBIqhATSp0CpRwDvcpTS4luTvVSFwVPK1jWlEVrV57/8CUcLCHSKFqeeqGOuFfYZkMPZikaStnXnAyOSYoRW7K3jsuM33zzIn3tpIV5kYxuYhycogGhnr6x0EiVK3F+fjR1GYq34mabu1Ah4S/hKJkGukt9dVxonKB2X9MU2FxJoz9jugG4qwZkj2YF2WK4dQ1hI2SZx6fSDFQWvC+AeomldymDTCnaQKBgQDeTnrd1c8pvMVGdxJ/KXlzccD0DUVfnISWI1iDPILb1jrE+Q8pmJi64mFemTLWRm1txjPZSdlHfWh0b1EzGbs32GBb/wQRgRs3vZez9/RHa9CoPl9wn46T4vrkpdWwH2pfzZcGx8n63PXN2nXM54X1avDcqgG0Won0V7f2pt/WjQKBgQDKwWGGlqaxfJ6Q8lgPYdjQehVEdg0kUdMBmK/MVG9jGx0ZZ8W3v6/lvkm3smiFoqlHKHTadAlO0mPSgN/MxEJS6hKGldKpK7qbbZJjgCraF0ef0P81ed79U26QzT0jQWQ9allNhI/fR5SkPOcR88aBFF6VmgqKi7I5gxntrCeTswKBgGG0lIPTaQ2rDfl+c/63t81pfajF/GudWxixyEFF4y3D5miUHnnlu5rHzIvLQXCz23yX1En4DytAjxw9oK6Y7JQK4a8NUjhvSgshpP9QR2jslfScFU5mkj+lTygXSufxpsscjPhJ7xf27YHgNk4so8/NhLc3cTBXarKdqxkZFuAxAoGAcfCcesLcaC9s+G2H+RT2f6mrm+5vUjK9Tk9cs4nbkqkhAC7l2G3bTlNSH+4deMPBNyDQEhdVR7tJp3kOy4aa8vdhc8psOO8/DatUsZ/L9QVSFcigMpALNPcW0j7UFaTgzPafusU9CK+0I1uqStdgfRdSU+Xd+J+qh1FaV8r/7qsCgYEAxoX9XAwI3yDsDPo7CpeWgkae5cB0mp+xtx/xkcRr3nergKIeREMb/u7yXDPm/YQi6QyFpiZFM7X94WPXU/FpKlpwhy4tbo67y8Az1zOOQ4HbDHbdUARD6VNbgeweF/QUHmrzItYc7FRmDRaP4D6k6cLeGR6se8IepxP4S7C7u0w=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhNkAkhcesRFeJoCUyHnNxRkyHXnsVQnF2qxHfUnxudQa9YZWyUP1xRI1KEpVz7ZB0QS7wgIIXQhheQR1Bwsg5AVZyldupuA+Z9ThfIA1/rEflJT/sjA0XcVssu48hFhkE0ZTKZpJcVY0JynIAKL5MhBJqkogr6mIaHNvvvdYKKpTFVDyIri2c8C3mLuyCyZ7Jaf+AZMsGch9XEM1IabDPNvix7Iaz7gA48GmPX30zvyV8xtqJQ0u4Lcfv4bbKM0YGNVGPwbCKfYZdg2Q1exVMT5n7yaVGW/Ud9OtDX4Mrhedntk0ymn3biTJupjL/tYgunj3QmDfyszVkxw2IdijLwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = " https://26b4-218-14-91-250.jp.ngrok.io/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://order.gulimall.com/aliPayReturnUrl";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout_express = "1m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    public  String pay(PayVo vo) throws AlipayApiException {
        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key,sign_type);
        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("total_amount", total_amount);
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("timeout_express",timeout_express);
        alipayRequest.setBizContent(bizContent.toString());
//        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
//                + "\"total_amount\":\""+ total_amount +"\","
//                + "\"subject\":\""+ subject +"\","
//                + "\"body\":\""+ body +"\","
//                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);
        return result;

    }

    public  String close(String  orderSn,String trade_no) throws AlipayApiException {
        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key,sign_type);
        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        if(trade_no !=null){
            bizContent.put("trade_no", trade_no);
        }
        if(orderSn !=null){
            bizContent.put("out_trade_no", orderSn);
        }
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("支付宝关单调用成功");
        } else {
            System.out.println("支付宝关单调用失败");
        }
        log.warn(response.getBody());
        return response.getBody();
    }


}
