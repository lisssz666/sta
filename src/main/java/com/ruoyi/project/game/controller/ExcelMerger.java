package com.ruoyi.project.game.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelMerger {
    public static void main(String[] args) {
        // 指定文件夹的路径
        String folderPath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/excel测试";

        // 创建一个新的工作簿
        Workbook newWorkbook = new XSSFWorkbook();
        Sheet newSheet = newWorkbook.createSheet("ExcelMerger");
        int rowIndex = 1;

        // 创建表头行
        Row headerRow = newSheet.createRow(0);
        // 添加表头列
        String[] headers = {"钻孔ID", "地层名称", "地层编码", "顶板埋深","底版埋深","地层厚度","地层描述"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
//            cell.setCellStyle(headerCellStyle);
        }

        // 获取文件夹中的所有文件
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xlsx")) {
                    try (Workbook workbook = WorkbookFactory.create(file)) {
                        // 获取第一个工作表
                        Sheet sheet = workbook.getSheetAt(0);

                        double lastResult =0.0;
                        // 遍历行，从第5行到第13行
                        for (int i = 3; i <= 8; i++) {
                            Row row = sheet.getRow(i);

                            // 判断是否存在M12列数据
                            if (row.getCell(12) != null) {
                                boolean hasData = false;
                                // 创建新行，并复制M5到M12列的单元格数据
                                Row newRow = newSheet.createRow(rowIndex);
//                                for (int j = 12; j <= 12; j++) {
                                    Cell sourceCell = row.getCell(12);
                                    Cell zkIdCell = sheet.getRow(4).getCell(0);
                                    //底版埋深
                                    Cell hightCell = sheet.getRow(8).getCell(0);
                                    Cell bottomDepthCell = sheet.getRow(i).getCell(14);
                                    double value1 = hightCell.getNumericCellValue();
                                    double value2 = bottomDepthCell.getNumericCellValue();
                                    double result = value1 - value2;

                                    //地层厚度
                                    double dcResult = result-lastResult;

                                    if (sourceCell != null) {
                                        switch (sourceCell.getCellType()) {
                                            case NUMERIC:
                                                newRow.createCell(0).setCellValue(zkIdCell.getNumericCellValue());
                                                newRow.createCell(1).setCellValue(sourceCell.getNumericCellValue());
                                                newRow.createCell(4).setCellValue(result);
                                                newRow.createCell(3).setCellValue(lastResult);
                                                lastResult=result;
                                                newRow.createCell(5).setCellValue(dcResult);
                                                hasData = true;
                                                break;
                                            case STRING:
                                                newRow.createCell(0).setCellValue(zkIdCell.getStringCellValue());
                                                newRow.createCell(1).setCellValue(sourceCell.getStringCellValue());
                                                newRow.createCell(4).setCellValue(result);
                                                newRow.createCell(3).setCellValue(lastResult);
                                                lastResult=result;
                                                newRow.createCell(5).setCellValue(dcResult);
                                                hasData = true;
                                                break;
                                            // 处理其他类型的单元格数据
                                            // ...
                                        }
                                    }
//                                }
                                if (hasData) {
                                    rowIndex++;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 将新工作簿保存到文件
            String outputFilePath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/excel测试/输出结果/respond3.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                newWorkbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





//String folderPath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/excel测试";
//String outputPath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/其他文档/excel测试/输出结果/respond.xlsx";
