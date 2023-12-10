package com.leovany.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果对象
 * @param <T>
 */
@Data
public class ResultVO<T> implements Serializable {

    /**
     * 错误码
     */
    private int code;
    /**
     * 内容
     */
    private T data;
    /**
     * 消息
     */
    private String message;
    /**
     * 描述
     */
    private String description;

    public ResultVO(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public ResultVO(int code, T data) {
        this(code,data,"","");
    }

    public ResultVO(int code, T data, String message) {
        this(code,data,message,"");
    }


}
