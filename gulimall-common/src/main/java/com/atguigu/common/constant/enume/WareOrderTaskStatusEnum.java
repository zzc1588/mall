package com.atguigu.common.constant.enume;

public enum WareOrderTaskStatusEnum {
    CREATE_NEW(0,"未回滚"),
    ORDER_TIMEOUT_UNLOCKED(1,"订单超时,库存回滚"),
    ORDER_CANCEL_UNLOCKED(2,"订单取消,库存回滚"),
    ORDER_EXCEPTION_UNLOCKED(3,"订单异常,库存回滚");
    private Integer code;
    private String msg;

    WareOrderTaskStatusEnum(Integer code, String msg) {
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
