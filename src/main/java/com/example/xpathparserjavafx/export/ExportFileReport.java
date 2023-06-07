package com.example.xpathparserjavafx.export;

import com.example.xpathparserjavafx.model.Cad;

import java.io.FileNotFoundException;

public class ExportFileReport implements ReportExportable{
    private final ExportToExcel exportToExcel;
    private  final TransferToExcel transferToExcel;

    public ExportFileReport() {
        this.exportToExcel = new ExportToExcel();
        this.transferToExcel = new TransferToExcel();
    }

    @Override
    public void export(Cad cad) throws FileNotFoundException {
        if(!cad.isTransfer()){
        this.exportToExcel.createExcelReportCad(cad);
        }else {
            this.transferToExcel.transferXMLToExcel(cad);
        }
    }
}
