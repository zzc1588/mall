package com.atguigu.gulimall.rabbitMq.constant;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-04 16:09
 **/
public enum IdWorkerConstant {
    MQ_WORKER_ID(1,"工作机器ID"),
    MQ_DATACENTER_ID(1,"数据中心ID");
    private Integer code;
    private String msg;

    IdWorkerConstant(Integer code, String msg) {
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
