package com.example.xpathparserjavafx.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegRecordOwner {
    private String cadNumber;
    //порядковый номер
    private String indexNumber;
    private String owner;
    private String regRecord;
    private String numberRegistrationRecord;
    private String share;
    private double shareNumerator;
    private double shareDenominator;
    private boolean changeStatus;
    private String typeOwnShare;
    private String cancelData;
    private StringBuilder underlyingDocument;
    private String changeRecordNumber;
    private StringBuilder changeUnderlyingDocuments;

    List<RegRecordEncumbrance> regRecordEncumbranceList;

    public RegRecordOwner() {
        this.changeStatus =true;
        this.underlyingDocument = new StringBuilder("");
        this.changeUnderlyingDocuments = new StringBuilder("");
        this.regRecordEncumbranceList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "RegRecordOwner{" +
                "cadNumber='" + cadNumber + '\'' +
                ", indexNumber='" + indexNumber + '\'' +
                ", owner='" + owner + '\'' +
                ", regRecord='" + regRecord + '\'' +
                ", numberRegistrationRecord='" + numberRegistrationRecord + '\'' +
                ", share='" + share + '\'' +
                ", shareNumerator=" + shareNumerator +
                ", shareDenominator=" + shareDenominator +
                ", changeStatus=" + changeStatus +
                ", typeOwnShare='" + typeOwnShare + '\'' +
                ", cancelData='" + cancelData + '\'' +
                ", underlyingDocument=" + underlyingDocument +
                ", changeRecordNumber='" + changeRecordNumber + '\'' +
                ", changeUnderlyingDocuments=" + changeUnderlyingDocuments +
                ", regRecordEncumbranceList=" + regRecordEncumbranceList +
                '}';
    }
}
