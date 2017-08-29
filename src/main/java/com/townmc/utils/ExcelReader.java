package com.townmc.utils;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.formula.FormulaParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;

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

                String cellValue = getCellValue(cell);

                values[c] = cellValue;
            }
            this.handleRow(r+1, values);
        }
    }

    private String getCellValue(Cell cell) {
        String cellValue = null;
        CellType cellType = cell.getCellTypeEnum();
        if(CellType.NUMERIC == cellType) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                SimpleDateFormat sdf = null;
                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                Date date = cell.getDateCellValue();
                cellValue = sdf.format(date);
            } else if (cell.getCellStyle().getDataFormat() == 58) {
                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                double value = cell.getNumericCellValue();
                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                cellValue = sdf.format(date);
            } else {
                double value = cell.getNumericCellValue();
                CellStyle style = cell.getCellStyle();
                DecimalFormat format = new DecimalFormat();
                String temp = style.getDataFormatString();
                // 单元格设置成常规，可以通过excel样式来还原数据，但是这里全都按普通来处理了，所以ifelse改成一样的了
                if (temp.equals("General")) {
                    format.applyPattern("#");
                } else {
                    format.applyPattern("#");
                }
                cellValue = format.format(value);
            }
        } else if(CellType.STRING == cellType) {
            cellValue = cell.getStringCellValue();
        } else if(CellType.BOOLEAN == cellType) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        } else if(CellType.FORMULA == cellType) {
            //cellValue = cell.getCellFormula();
                /*
                try {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    cellValue = String.valueOf(cell.getRichStringCellValue());
                }
                */
            try {
                Workbook wb = cell.getSheet().getWorkbook();
                CreationHelper crateHelper = wb.getCreationHelper();
                FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
                cellValue = getCellValue(evaluator.evaluateInCell(cell));
            } catch (FormulaParseException e) {
                cellValue = cell.getCellFormula();
            }
        } else if(CellType.BLANK == cellType) {
            cellValue = "";
        } else if(CellType.ERROR == cellType) {
            cellValue = "非法字符";
        } else {
            cellValue = "未知类型";
        }

        return cellValue;

    }
}