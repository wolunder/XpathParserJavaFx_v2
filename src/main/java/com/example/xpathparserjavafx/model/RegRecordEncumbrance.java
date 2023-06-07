package com.example.xpathparserjavafx.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RegRecordEncumbrance {
    private String regNumberEncumbrance;
    private String dataRegEncumbrance;
    private String typeEncumbrance;
    private String duration;
    private String encumbranceOwner;
    private StringBuilder docFound;
    private List<String> cadRegRecordList;

    public RegRecordEncumbrance(){
        this.cadRegRecordList = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "RegRecordEncumbrance{" +
                "regNumberEncumbrance='" + regNumberEncumbrance + '\'' +
                ", dataRegEncumbrance='" + dataRegEncumbrance + '\'' +
                ", typeEncumbrance='" + typeEncumbrance + '\'' +
                ", duration='" + duration + '\'' +
                ", encumbranceOwner='" + encumbranceOwner + '\'' +
                ", docFound=" + docFound +
                ", cadRegRecordList=" + cadRegRecordList +
                '}';
    }
}
