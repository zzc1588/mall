package com.atguigu.gulimall.rabbitMq.constant;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:49
 **/
public enum MessageStatusConstant {
    MQ_MESSAGE_CREATE(0,"消息无异常状态，成功回调不修改状态"),
    MQ_CONFIRM_CALLBACK_ERROR(1,"消息发送到broker失败"),
    MQ_RETURN_CALLBACK_ERROR(2,"消息发送到queue失败"),
    MQ_CONSUMER_ERROR(3,"消息消费失败");

    private Integer code;
    private String msg;

    MessageStatusConstant(Integer code, String msg) {
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
