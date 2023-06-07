package com.example.xpathparserjavafx.export;


import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class TransferToExcel {

    private static final Logger LOGGER = Logger.getLogger(TransferToExcel.class.getName());

    private static HSSFCellStyle createCellStyleForTitle(HSSFWorkbook workbook){

        HSSFFont font =  workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();

        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return style;
    }

    private static HSSFCellStyle createCellStyleForBody(HSSFWorkbook workbook){
        HSSFFont font =  workbook.createFont();
        HSSFCellStyle style1 = workbook.createCellStyle();

        style1.setFont(font);

        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style1.setBorderRight(BorderStyle.THIN);
        style1.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style1.setBorderTop(BorderStyle.THIN);
        style1.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return style1;
    }

    public void transferXMLToExcel(Cad cad){
        HSSFWorkbook excelReportCad = new HSSFWorkbook();
        HSSFSheet sheet= excelReportCad.createSheet("Правообладатели");
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        int rownum = 0;
        Cell cell;
        Row row;

        HSSFCellStyle style = createCellStyleForTitle(excelReportCad);
        HSSFCellStyle styleBody =createCellStyleForBody(excelReportCad);
        row = sheet.createRow(rownum);

        //хедер
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(style);
        sheet.setColumnWidth(0, 5000);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(style);
        sheet.setColumnWidth(0, 5000);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Правообладатель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(1, 17000);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Рег. запись");
        cell.setCellStyle(style);
        sheet.setColumnWidth(2, 20000);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Документ основание");
        cell.setCellStyle(style);
        sheet.setColumnWidth(3, 20000);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Дата прекращения записи");
        cell.setCellStyle(style);
        sheet.setColumnWidth(4, 20000);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Рег. запись перехода");
        cell.setCellStyle(style);
        sheet.setColumnWidth(5, 20000);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Документ основание перехода");
        cell.setCellStyle(style);
        sheet.setColumnWidth(6, 20000);

        for(RegRecordOwner o: cad.getListOwner()){
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(o.getIndexNumber());
            cell.setCellStyle(styleBody);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(o.getOwner());
            cell.setCellStyle(styleBody);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(o.getRegRecord());
            cell.setCellStyle(styleBody);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(o.getUnderlyingDocument().toString());
            cell.setCellStyle(styleBody);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(o.getCancelData());
            cell.setCellStyle(styleBody);

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(o.getChangeRecordNumber());
            cell.setCellStyle(styleBody);

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(o.getChangeUnderlyingDocuments().toString());
            cell.setCellStyle(styleBody);
        }

        //запись в excel
        String pattern = "dd-MM-YYYY";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String fileNameExcel = "C:/конвертированные выписки/переход права "+cad.getCadNumber().replaceAll(":","-")+" от "+simpleDateFormat.format(date)+".xls";
        File file = new File(fileNameExcel);
        file.getParentFile().mkdirs();

        try(FileOutputStream outFile = new FileOutputStream(file)) {

            excelReportCad.write(outFile);
        }
        catch (IOException e) {
            LOGGER.info("ошибка выгрузки");
        }
    }
}
