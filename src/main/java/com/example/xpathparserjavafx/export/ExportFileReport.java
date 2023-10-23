package com.example.xpathparserjavafx.export;

import com.example.xpathparserjavafx.model.Cad;

import java.io.FileNotFoundException;

public class ExportFileReport implements ReportExportable{
    private final ExportToExcel exportToExcel;
    private  final TransferToExcel transferToExcel;
    private final FarmPropertiesToExcel farmPropertiesToExcel;

    public ExportFileReport() {
        this.exportToExcel = new ExportToExcel();
        this.transferToExcel = new TransferToExcel();
        this.farmPropertiesToExcel = new FarmPropertiesToExcel();
    }

    @Override
    public void export(Cad cad) throws FileNotFoundException {
        if(!cad.isTransfer()){
            if(!cad.isFarmRealEstate()){
                this.exportToExcel.createExcelReportCad(cad);
            }else {
                this.farmPropertiesToExcel.createReportToExcel(cad);
            }
        }else {
            this.transferToExcel.transferXMLToExcel(cad);
        }
    }
}
