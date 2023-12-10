package com.leovany.usercenter.exception;

import com.leovany.usercenter.common.ResultVO;
import com.leovany.usercenter.common.ErrorCode;
import com.leovany.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理异常-BusinessException
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO<?> businessExceptionHandler(BusinessException e){
        log.error("businessException:" + e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    /**
     * 处理异常-RuntimeException
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVO<?> runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException:" + e);
        return ResultUtils.error(ErrorCode.ERROR_SYSTEM,e.getMessage());
    }



}
