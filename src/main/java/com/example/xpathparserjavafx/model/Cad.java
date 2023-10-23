package com.example.xpathparserjavafx.model;

import lombok.Data;

import java.util.List;

@Data
public class Cad {
    private String cadNumber;
    private String farm;
    private String area;
    private boolean isTransfer;
    private boolean isFarmRealEstate;
    private List<RegRecordOwner> listOwner;
    private List<FarmRealEstate> farmRealEstateList;

    public Cad() {
        this.isTransfer = false;
        this.isFarmRealEstate = false;
    }
}

