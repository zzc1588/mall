package com.atguigu.common.valid;

import com.google.common.base.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Object> {

    private Set<Object> set = new HashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }
    //判断是否校验成功
    /**
     *
     * @param value 需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
