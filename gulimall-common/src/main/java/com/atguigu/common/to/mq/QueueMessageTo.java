package com.atguigu.common.to.mq;

import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.SelectGroup;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateGroup;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 16:40
 **/
@Data
@ToString
public class QueueMessageTo {
    @NotBlank(message = "toExchange不能为空")
    private String toExchange;
    @NotBlank(message = "routingKey不能为空",groups = {SelectGroup.class,UpdateGroup.class})
    private String routingKey;
    @NotNull(message = "content不能为空",groups = {SelectGroup.class, UpdateGroup.class})
    private Object content;
    private String classType;
}
