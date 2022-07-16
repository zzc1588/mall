package com.atguigu.common.validator.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-05 22:16
 **/
public class WorkTimeValidated implements ConstraintValidator<WorkTime, Integer> {
    private int max;
    @Override
    public void initialize(WorkTime work) {
        max = work.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        Boolean flag = false;
        if (value == null || value < max) {
            flag = true;
        }
        return flag;
    }
}