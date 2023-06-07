package com.example.xpathparserjavafx.export;


import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordEncumbrance;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import com.example.xpathparserjavafx.parser.InterpretationRecords;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportToExcel {

    private static HSSFCellStyle createCellStyleForTitle(HSSFWorkbook workbook) {

        HSSFFont font = workbook.createFont();
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

    private static HSSFCellStyle createCellStyleForBody(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
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

    public void createExcelReportCad(Cad cad) throws FileNotFoundException {

        HSSFWorkbook excelReportCad = new HSSFWorkbook();
        HSSFSheet sheet = excelReportCad.createSheet("Правообладатели");
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        int rownum = 0;
        Cell cell;
        Row row;

        HSSFCellStyle style = createCellStyleForTitle(excelReportCad);
        HSSFCellStyle styleBody = createCellStyleForBody(excelReportCad);
        row = sheet.createRow(rownum);

        //хедер таблицы
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(style);
        sheet.setColumnWidth(0, 5000);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Правообладатель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(1, 17000);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Тип доли");
        cell.setCellStyle(style);
        sheet.setColumnWidth(2, 5000);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Рег. запись");
        cell.setCellStyle(style);
        sheet.setColumnWidth(3, 20000);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Числитель/знаменатель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(4, 7500);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Числитель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(5, 5000);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Знаменатель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(6, 5000);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Посчитанная доля");
        cell.setCellStyle(style);
        sheet.setColumnWidth(7, 5000);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Обременение");
        cell.setCellStyle(style);
        sheet.setColumnWidth(8, 10000);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Тип");
        cell.setCellStyle(style);
        sheet.setColumnWidth(9, 10000);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Срок обременения");
        cell.setCellStyle(style);
        sheet.setColumnWidth(10, 10000);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Обременитель");
        cell.setCellStyle(style);
        sheet.setColumnWidth(11, 15000);


        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Документ- основание");
        cell.setCellStyle(style);
        sheet.setColumnWidth(12, 25000);

        String owner = null;
//      тело таблицы
        if (cad.getListOwner() != null){
            for (RegRecordOwner o : cad.getListOwner()) {
                rownum++;
                row = sheet.createRow(rownum);

                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(o.getIndexNumber());
                cell.setCellStyle(styleBody);

                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(o.getOwner());
                cell.setCellStyle(styleBody);

                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(InterpretationRecords.rentOrOwnRecord(o, InterpretationRecords.getOwnerStr(o)));
                cell.setCellStyle(styleBody);

                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(o.getRegRecord());
                cell.setCellStyle(styleBody);

                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(o.getShare());
                cell.setCellStyle(styleBody);

                cell = row.createCell(5, CellType.NUMERIC);
                cell.setCellValue(o.getShareNumerator());
                cell.setCellStyle(styleBody);

                cell = row.createCell(6, CellType.NUMERIC);
                cell.setCellValue(o.getShareDenominator());
                cell.setCellStyle(styleBody);

                cell = row.createCell(7, CellType.NUMERIC);
                cell.setCellStyle(styleBody);

                if (o.getShare() != null && o.getShareDenominator() == 0 && o.getShareNumerator() != 0) {
                    cell.setCellValue(o.getShareNumerator());
                } else if (o.getShare() != null && o.getShareDenominator() != 0 && o.getShare().contains("/")) {
                    cell.setCellValue(InterpretationRecords.calculationNumerator(o.getShare().trim()));
                } else {
                    cell.setCellValue(0);
                }

                StringBuilder strCell8 = new StringBuilder("");
                StringBuilder strCell9 = new StringBuilder("");
                StringBuilder strCell10 = new StringBuilder("");
                StringBuilder strCell11 = new StringBuilder("");
                StringBuilder strCell12 = new StringBuilder("");

                for (RegRecordEncumbrance re : o.getRegRecordEncumbranceList()) {
                    strCell8.append("- " + re.getRegNumberEncumbrance() + "\n");
                    strCell9.append("- " + re.getTypeEncumbrance() + "\n");
                    strCell10.append("- " + re.getDuration() + "\n");
                    strCell11.append("- " + re.getEncumbranceOwner() + "\n");
                    strCell12.append("- " + re.getDocFound() + "\n");
                }
                cell = row.createCell(8, CellType.STRING);
                cell.setCellValue(strCell8.toString());
                cell.setCellStyle(styleBody);

                cell = row.createCell(9, CellType.STRING);
                cell.setCellValue(strCell9.toString());
                cell.setCellStyle(styleBody);

                cell = row.createCell(10, CellType.STRING);
                cell.setCellValue(strCell10.toString());
                cell.setCellStyle(styleBody);

                cell = row.createCell(11, CellType.STRING);
                cell.setCellValue(strCell11.toString());
                cell.setCellStyle(styleBody);

                cell = row.createCell(12, CellType.STRING);
                cell.setCellValue(strCell12.toString());
                cell.setCellStyle(styleBody);
            }
            String pattern = "dd-MM-YYYY";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String fileName = cad.getCadNumber().replaceAll(":", "-");

        File file = new File("C:/конвертированные выписки/" + fileName +" от "+simpleDateFormat.format(date)+ ".xls");
        file.getParentFile().mkdirs();

        try (FileOutputStream outFile = new FileOutputStream(file)) {

            excelReportCad.write(outFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            InterpretationRecords.reset();
        }
    }

}


}
