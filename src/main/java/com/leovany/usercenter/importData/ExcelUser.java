package com.leovany.usercenter.importData;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 表格用户信息
 */
@Data
public class ExcelUser {

    /**
     * id
     */
    @ExcelProperty("编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;

}
