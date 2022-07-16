package com.atguigu.gulimall.ware.constant.enume;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-27 11:53
 **/
public enum OrderTaskDetailStatusEnum {
    TASK_DETAIL_LOCKED(1,"已锁定"),
    TASK_DETAIL_UNLOCKED(2,"已解锁"),
    TASK_DETAIL_DEDUCTION(3,"扣减");
    private Integer code;
    private String msg;

    OrderTaskDetailStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
