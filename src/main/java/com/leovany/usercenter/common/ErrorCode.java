package com.leovany.usercenter.common;

/**
 * 错误代码
 * 错误码
 *
 * @author leovany
 * @date 2023/09/23
 */
public enum ErrorCode {
    SUCCESS(0, "success", ""),
    ERROR_PARAMS(40000, "请求参数错误", ""),
    ERROR_NULL(40001, "请求数据为空", ""),
    ERROR_NOT_LOGIN(40100, "未登录", ""),
    ERROR_NO_AUTH(41001, "无权限", ""),
    ERROR_SYSTEM(50000, "系统内部异常", "")
    ;


    /**
     * 错误码ID
     */
    private final int code;

    /**
     * 错误码信息
     */
    private final String message;

    /**
     * 错误码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
