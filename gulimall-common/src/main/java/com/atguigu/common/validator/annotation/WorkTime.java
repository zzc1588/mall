package com.atguigu.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-05 22:07
 **/
@Constraint(validatedBy = { WorkTimeValidated.class })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface WorkTime {
    int max() default 8;

    String message() default "工作时间不能超过8小时!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}