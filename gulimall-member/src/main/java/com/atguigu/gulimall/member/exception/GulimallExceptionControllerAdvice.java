package com.atguigu.gulimall.member.exception;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.UnexpectedTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * 集中处理所有异常
 */
@Slf4j
@RestControllerAdvice
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R handleException(MethodArgumentNotValidException e){
        log.error("数据校验异常{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,Object> errorMap = new HashMap<>();
        extracted(bindingResult, errorMap);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",errorMap);
    }
    @ExceptionHandler({BindException.class})
    public R handleException(BindException e){
        log.error("表单数据校验异常{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,Object> errorMap = new HashMap<>();
        extracted(bindingResult, errorMap);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",errorMap);
    }

    /**
     * 封装参数字段 和 错误信息和map集合（支持嵌套对象校验）
     * @param bindingResult
     * @param errorMap
     */
    private void extracted(BindingResult bindingResult, Map<String, Object> errorMap) {
        bindingResult.getFieldErrors().forEach((fieldError)->{
            if (fieldError.getField().contains(".")){
                String[] split = fieldError.getField().split("\\.");
                HashMap<String, Object> nestedMap;
                if(errorMap.get(split[0])!=null){
                    nestedMap = (HashMap<String, Object>) errorMap.get(split[0]);
                }else {
                    nestedMap = new HashMap<>();
                }
                nestedMap.put(split[1],fieldError.getDefaultMessage());
                errorMap.put(split[0], nestedMap);
            }else {
                errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
        });
    }
    //类型转换异常
    @ExceptionHandler(ClassCastException.class)
    public R handleException(ClassCastException e) {
        log.error("类型转化异常:{}",e.getMessage());
        return R.error(BizCodeEnume.CLASS_CAST_EXCEPTION.getCode(), BizCodeEnume.CLASS_CAST_EXCEPTION.getMsg());
    }
    //类型转换异常
    @ExceptionHandler(UnexpectedTypeException.class)
    public R handleException(UnexpectedTypeException e) {
        log.error("类型转化异常:{}",e.getMessage());
        return R.error(BizCodeEnume.CLASS_CAST_EXCEPTION.getCode(), BizCodeEnume.CLASS_CAST_EXCEPTION.getMsg());
    }


    @ExceptionHandler(value= RejectedExecutionException.class)
    public R handleException(RejectedExecutionException e){
        log.error("并发过大，并发拒绝策略执行{}，异常类型：{}",e.getMessage(),e.getClass());
        return R.error(BizCodeEnume.CONCURRENT_ABORT_EXCEPTION.getCode(),BizCodeEnume.CONCURRENT_ABORT_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public R handleException(HttpMessageNotReadableException notReadableException){
        log.error("错误：",notReadableException);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误：",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        log.error("错误信息：",e);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
