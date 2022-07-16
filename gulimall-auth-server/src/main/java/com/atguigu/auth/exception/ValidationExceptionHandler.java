package com.atguigu.auth.exception;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 23:47
 **/
@RestControllerAdvice
@Slf4j
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    R handleConstraintViolation(ConstraintViolationException exception) {
        List<Object> args = exception.getConstraintViolations().stream().map(val -> val.getInvalidValue()).collect(Collectors.toList());
        List<String> message = exception.getConstraintViolations().stream().map(msg -> msg.getMessage()).collect(Collectors.toList());

        log.error("数据校验出现问题{}，异常类型：{},异常参数值：{}",exception.getMessage(),exception.getClass(),args);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),message+",异常参数值："+args);
    }

    @ExceptionHandler(FeignException.class)
    R handleConstraintViolation(FeignException exception) {
        log.error("远程调用出现问题{}，异常类型：{}",exception.getMessage(),exception.getClass());
        return R.error(BizCodeEnume.FEIGN_EXCEPTION.getCode(),BizCodeEnume.FEIGN_EXCEPTION.getMsg());
    }

}
