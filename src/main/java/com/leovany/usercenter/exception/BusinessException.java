package com.leovany.usercenter.exception;


import com.leovany.usercenter.common.ErrorCode;

/**
 * 业务异常
 * 自定义业务异常类
 *
 * @author leovany
 * @date 2023/09/23
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 业务异常
     *
     * @param message     信息
     * @param code        错误码
     * @param description 描述
     */
    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 业务异常
     *
     * @param errorCode 错误代码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    /**
     * 业务异常
     *
     * @param errorCode   错误代码
     * @param description 描述
     */
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
