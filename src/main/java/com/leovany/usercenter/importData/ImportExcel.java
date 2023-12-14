package com.leovany.usercenter.importData;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ImportExcel {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String userExcelPath = projectPath + "/src/main/resources/excel/userExcel.xlsx";
        //readByListener(userExcelPath);
        synchronousRead(userExcelPath);
    }

    /**
     * 监听器读取
     *
     * @param filePath
     */
    public static void readByListener(String filePath) {
        EasyExcel.read(filePath, ExcelUser.class, new ExcelUserListener()).sheet().doRead();
    }

    /**
     * 同步读
     *
     * @param filePath
     */
    public static void synchronousRead(String filePath) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<ExcelUser> totalDataList = EasyExcel.read(filePath).head(ExcelUser.class).sheet().doReadSync();
        for (ExcelUser xingQiuTableUserInfo : totalDataList) {
            System.out.println(xingQiuTableUserInfo);
        }
    }
}
