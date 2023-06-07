package com.example.xpathparserjavafx.model;

import lombok.Data;

import java.util.List;

@Data
public class Cad {
    private String cadNumber;
    private String area;
    private boolean isTransfer;
    private List<RegRecordOwner> listOwner;

    public Cad() {
        this.isTransfer = false;
    }
}

