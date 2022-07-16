package com.atguigu.gulimall.rabbitMq.entity;

import com.atguigu.common.validator.annotation.WorkTime;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-05 20:49
 **/
@Data
@ToString
public class TestEntity {
    @Valid
    @NotNull
    private MqMessageEntity mqMessageEntity;
    @Valid
    @NotNull
    private MqMessageEntity mqMessageEntity2;
    @NotBlank
    private String content;
    @Min(value = 10)
    private Integer count;
    @DecimalMax(value = "10")
    private BigDecimal bigDecimal;
    @Pattern(regexp = "[a-z]{1}",message = "只允许a-z之间的一个字符")
    private String pattern;
    @Length(min = 1,max = 10)
    private String length;
    @WorkTime(max = 7,message = "工作时间不能超过7小时")
    private Integer workTime;
}
