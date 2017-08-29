package com.townmc.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 读取excel的抽象类
 * 实现类继承该类后，实现hendleRow方法，并执行execute方法即可
 */

abstract class ExcelReader {

    /**
     * 对某一行进行自定义的处理
     * @param rowNum 当前行号
     * @param row 从excel中解析出来的行数据
     */
    abstract void handleRow(int rowNum, Object[] row);

    /**
     * 处理excel的行数据
     * @param fileName String excel文件名
     * @param skipRow 需要跳过的行号（不读取的行）
     */
    protected void execute(String fileName, int[] skipRow) throws IOException {
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);

        Workbook wb = null;
        if("xls".equals(suffix)) {
            wb = new HSSFWorkbook(new FileInputStream(fileName));
        } else if("xlsx".equals(suffix)) {
            wb = new XSSFWorkbook(new FileInputStream(fileName));
        }

        Sheet sheet = wb.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();

        // 仅拿第一行的列数做标准
        int cellCount = rows > 0 ? sheet.getRow(0).getPhysicalNumberOfCells() : 0;
        outter:
        for (int r = 0; r < rows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            // 是否为需要跳过的行
            inner:
            for(int s = 0; s < skipRow.length; s++) {
                if(skipRow[s]-1 == r) {
                    continue outter;
                }
            }

            Object[] values = new Object[cellCount];

            for (int c = 0; c < cellCount; c++) {
                values[c] = null;

                Cell cell = row.getCell(c);
                if (cell == null) {
                    continue;
                }

                String cellValue = ExcelUtil.getCellValue(cell);

                values[c] = cellValue;
            }
            this.handleRow(r+1, values);
        }
    }

}