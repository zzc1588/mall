package com.atguigu.gulimall.order.entity.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-24 15:07
 **/
@ToString
public class OrderConfirmVo {
    @Getter @Setter
    private List<MemberAddressVos> memberAddressVos;
    @Getter @Setter
    private List<OrderItemVo> items;
    //发票记录...
    //优惠卷信息...
    /**
     * 积分
     */
    @Getter @Setter
    private Integer integration;
    @Getter @Setter
    private String orderToken; //订单防重令牌
    @Getter @Setter
    Map<Long,Boolean> stocks;

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
//    BigDecimal total;
    public BigDecimal getTotal(){
        BigDecimal num = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal bigDecimal = item.getPrice().multiply(new BigDecimal(item.getCount()));
                num = num.add(bigDecimal);
            }
        }
        return num;
    }

//    BigDecimal payPrice;
    public BigDecimal getPayPrice(){
        return getTotal();
    }

    @Data
    public static class MemberAddressVos{
        private Long id;
        /**
         * member_id
         */
        private Long memberId;
        /**
         * 收货人姓名
         */
        private String name;
        /**
         * 电话
         */
        private String phone;
        /**
         * 邮政编码
         */
        private String postCode;
        /**
         * 省份/直辖市
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 区
         */
        private String region;
        /**
         * 详细地址(街道)
         */
        private String detailAddress;
        /**
         * 省市区代码
         */
        private String areacode;
        /**
         * 是否默认
         */
        private Integer defaultStatus;
    }

}
