package com.atguigu.gulimall.order.entity.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-04 20:54
 **/
@Data
@ToString
public class PaySyncVo {

    //商家网站唯一订单号。
    private String out_trade_no;
    //该交易在支付宝系统中的交易流水号。最长 64 位。
    private String trade_no;
    //支付宝分配给开发者的应用 APPID。
    private String app_id;
    //收款支付宝账号对应的支付宝唯一用户号。以 2088 开关的纯 16 位数字
    private String seller_id;
    //处理结果的描述，信息来自于 code 返回结果的描述。
    private String msg;
    //编码格式。
    private String charset;
    //时间。
    private String timestamp;
    //结果码，
    private String code;
    //该笔订单的资金总额，单位为 RMB-Yuan。取值范围 [0.01, 100000000.00]，精确到小数点后两位。
    private BigDecimal total_amount;

}
