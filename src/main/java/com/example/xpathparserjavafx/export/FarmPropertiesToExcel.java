package com.example.xpathparserjavafx.export;

import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.FarmRealEstate;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FarmPropertiesToExcel {

    public void createReportToExcel(Cad cad) throws FileNotFoundException {
        HSSFWorkbook excelReportCad = new HSSFWorkbook();
        HSSFSheet sheet = excelReportCad.createSheet("Зарегистрированная недвижимость");
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        int rownum = 0;
        Cell cell;
        Row row;

        HSSFCellStyle style = ParamExcel.createCellStyleForTitle(excelReportCad);
        HSSFCellStyle styleBody = ParamExcel.createCellStyleForBody(excelReportCad);
        row = sheet.createRow(rownum);

        //хедер
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(style);
        sheet.setColumnWidth(0, 1500);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Вид объекта недвижимости");
        cell.setCellStyle(style);
        sheet.setColumnWidth(1, 5000);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Кадастровый номер");
        cell.setCellStyle(style);
        sheet.setColumnWidth(2, 5000);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Назначение объекта недвижимости");
        cell.setCellStyle(style);
        sheet.setColumnWidth(3, 7500);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Виды разрешенного использования объекта недвижимости");
        cell.setCellStyle(style);
        sheet.setColumnWidth(4, 9000);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Местоположение");
        cell.setCellStyle(style);
        sheet.setColumnWidth(5, 9000);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Площадь");
        cell.setCellStyle(style);
        sheet.setColumnWidth(6, 2500);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Вид права, доля в праве");
        cell.setCellStyle(style);
        sheet.setColumnWidth(7, 9000);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Дата государственной регистрации");
        cell.setCellStyle(style);
        sheet.setColumnWidth(8, 2500);

        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Номер государственной регистрации");
        cell.setCellStyle(style);
        sheet.setColumnWidth(9, 9000);

        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Основание государственной регистрации");
        cell.setCellStyle(style);
        sheet.setColumnWidth(10, 9000);

        cell = row.createCell(11, CellType.STRING);
        cell.setCellValue("Дата государственной регистрации прекращения права");
        cell.setCellStyle(style);
        sheet.setColumnWidth(11, 9000);

        cell = row.createCell(12, CellType.STRING);
        cell.setCellValue("Ограничение права и обременение объекта недвижимости");
        cell.setCellStyle(style);
        sheet.setColumnWidth(12, 9000);

        cell = row.createCell(13, CellType.STRING);
        cell.setCellValue("Вид ограничения");
        cell.setCellStyle(style);
        sheet.setColumnWidth(13, 5000);

        cell = row.createCell(14, CellType.STRING);
        cell.setCellValue("номер государственной регистрации");
        cell.setCellStyle(style);
        sheet.setColumnWidth(14, 7500);

       List<FarmRealEstate> farmPropertiesList =  cad.getFarmRealEstateList();

        for (int i = 0; i < farmPropertiesList.size(); i++) {
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i+1);
            cell.setCellStyle(styleBody);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getTypeProperty());
            cell.setCellStyle(styleBody);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getCadNumber());
            cell.setCellStyle(styleBody);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getPurposeOfProperty());
            cell.setCellStyle(styleBody);

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getPermittedUse());
            cell.setCellStyle(styleBody);

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getAddress());
            cell.setCellStyle(styleBody);

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getArea());
            cell.setCellStyle(styleBody);

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getTypeRightAndShare());
            cell.setCellStyle(styleBody);

            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getDataReg());
            cell.setCellStyle(styleBody);

            cell = row.createCell(9, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getNumberReg());
            cell.setCellStyle(styleBody);

            cell = row.createCell(10, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getDocumentRight());
            cell.setCellStyle(styleBody);

            cell = row.createCell(11, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getCancelRight());
            cell.setCellStyle(styleBody);

            cell = row.createCell(12, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getEncumbranceRight());
            cell.setCellStyle(styleBody);

            cell = row.createCell(13, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getTypeEncumbrance());
            cell.setCellStyle(styleBody);

            cell = row.createCell(14, CellType.STRING);
            cell.setCellValue(farmPropertiesList.get(i).getNumberRegistrationEncumbrance());
            cell.setCellStyle(styleBody);

            String pattern = "dd-MM-YYYY";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            File file = new File("C:/конвертированные выписки/выписка по "+cad.getFarm()+" от "+simpleDateFormat.format(date)+ ".xls");
            file.getParentFile().mkdirs();

            try (FileOutputStream outFile = new FileOutputStream(file)) {
                excelReportCad.write(outFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
