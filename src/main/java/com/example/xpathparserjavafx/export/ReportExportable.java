package com.example.xpathparserjavafx.export;

import com.example.xpathparserjavafx.model.Cad;

import java.io.FileNotFoundException;

public interface ReportExportable {
    void export(Cad cad) throws FileNotFoundException;
}
