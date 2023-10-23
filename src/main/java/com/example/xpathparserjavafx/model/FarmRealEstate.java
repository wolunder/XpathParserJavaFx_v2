package com.example.xpathparserjavafx.model;

import lombok.Data;

import java.awt.*;
import java.sql.Struct;

@Data
public class FarmRealEstate {
    private String typeProperty;
    private String cadNumber;
    private String purposeOfProperty;// назначение объекта недвижимости
    private String permittedUse;// разрешеное использование
    private String address;
    private String area;
    private String typeRightAndShare;
    private String dataReg;
    private String numberReg;
    private String documentRight;
    private String cancelRight;// прекращение права
    private String encumbranceRight;//ограничение прав
    private String typeEncumbrance;
    private String numberRegistrationEncumbrance;

    @Override
    public String toString() {
        return "FarmProperty{" +
                "typeProperty='" + typeProperty + '\'' +
                "\n, cadNumber='" + cadNumber + '\'' +
                "\n, purposeOfProperty='" + purposeOfProperty + '\'' +
                "\n, permittedUse='" + permittedUse + '\'' +
                "\n, address='" + address + '\'' +
                "\n, area='" + area + '\'' +
                "\n, typeRightAndShare='" + typeRightAndShare + '\'' +
                "\n, dataReg='" + dataReg + '\'' +
                "\n, numberReg='" + numberReg + '\'' +
                "\n, documentRight='" + documentRight + '\'' +
                "\n, cancelRight='" + cancelRight + '\'' +
                "\n, encumbranceRight='" + encumbranceRight + '\'' +
                "\n, typeEncumbrance='" + typeEncumbrance + '\'' +
                "\n, numberRegistrationEncumbrance='" + numberRegistrationEncumbrance+ '\'' +
                '}';
    }
}
