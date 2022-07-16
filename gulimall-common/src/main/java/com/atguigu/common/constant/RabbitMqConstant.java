package com.atguigu.common.constant;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-07 21:21
 **/
public class RabbitMqConstant {
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String DELAY_EVENT_ROUTING_KEY = "order.create";

    public static final String ORDER_RELEASE_QUEUE = "order.release.order.queue";
    public static final String RELEASE_EVENT_ROUTING_KEY = "order.release.#";

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";


    public static final String MEMBER_COMMON_QUEUE = "member-common-queue";
    public static final String MEMBER_EVENT_ROUTING_KEY = "member.common";
    public static final String MEMBER_EVENT_EXCHANGE = "member-event-exchange";
}
