package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-13 22:45
 **/
public class Cart {
    List<CartItem> items;  //购物项
    private Integer countType;  //商品种类
    private Integer countNum; //商品总数
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//减免的价格
    private Boolean checkAll;
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Boolean getCheckAll() {
        if(items!=null && items.size()>0){
            for (CartItem item : items) {
                if(!item.getCheck()){
                    return false;
                }
            }
        }else {
            return false;
        }

        return true;
    }

    /**
     * 获取购物车商品种类
     * @return
     */
    public Integer getCountType() {
        int count = 0;
        if(items !=null && items.size() >0){
            for (CartItem item : items) {
                count += 1;
            }

        }
        return count;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    /**
     * 获取购物车商品总数
     * @return
     */
    public Integer getCountNum() {
        int count = 0;
        if(items !=null && items.size() >0){
            for (CartItem item : items) {
                count += item.getCount();
            }

        }
        return count;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    /**
     * 获取购物车商品总价
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        //计算总价
        if (items!=null && items.size()>0){
            for (CartItem item : items) {
                if(item.getCheck()){
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        //计算折扣后的价格
        BigDecimal subtract = amount.subtract(getReduce());

        return subtract;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
