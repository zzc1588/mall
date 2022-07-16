package com.atguigu.gulimall.rabbitMq.entity;

import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:26
 **/
@Data
@TableName("mq_message")
public class MqMessageEntity {
    @TableId
    private Long messageId;
    @NotBlank
    private String content;
    @NotBlank
    private String toExchange;
    @NotBlank
    private String routingKey;
    @NotBlank(groups = {UpdateGroup.class})
    private String replyText;
    @NotBlank(groups = {UpdateGroup.class})
    private String classType;
    /**
     * '0-新建 1-已发送 2-错误抵达 3-已抵达'
     */
    @NotNull
    @Min(value = 0)  @Max(value = 3)
    private Integer messageStatus;
    private Date createTime;
    private Date updateTime;
}
