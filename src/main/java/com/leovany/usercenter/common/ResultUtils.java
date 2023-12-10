package com.leovany.usercenter.common;

/**
 * 返回响应对象工具类
 *
 * @author leovany
 * @date 2023/09/23
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@link ResultVO}<{@link T}>
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<T>(0, data, "success");
    }

    /**
     * 成功
     *
     * @return {@link ResultVO}<{@link T}>
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<T>(0, null, "success");
    }


    /**
     * 错误
     *
     * @param code        错误码
     * @param message     信息
     * @param description 描述
     * @return {@link ResultVO}
     */
    public static ResultVO error(int code, String message, String description) {
        return new ResultVO<>(code, null, message, description);
    }

    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @return {@link ResultVO}
     */
    public static ResultVO error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage(), errorCode.getDescription());
    }

    /**
     * 错误
     *
     * @param errorCode   错误代码
     * @param description 描述
     * @return {@link ResultVO}
     */
    public static ResultVO error(ErrorCode errorCode, String description) {
        return error(errorCode.getCode(), errorCode.getMessage(), description);
    }

}
