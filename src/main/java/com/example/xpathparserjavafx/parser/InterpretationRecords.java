package com.example.xpathparserjavafx.parser;


import com.example.xpathparserjavafx.model.RegRecordOwner;
import lombok.Getter;


public class InterpretationRecords {
    private static int factorDenumerator;

    public static String getNameFileXml() {
        return nameFileXml;
    }

    public static void setNameFileXml(String nameFileXml) {
        InterpretationRecords.nameFileXml = nameFileXml;
    }

    private static String nameFileXml;

    private static String stringOwner;

    public static String getStringOwner() {
        return stringOwner;
    }

    public static void setStringOwner(String stringOwner) {
        InterpretationRecords.stringOwner = stringOwner;
    }

    public static void setFactorDenumerator(int factorDenumerator) {
        InterpretationRecords.factorDenumerator = factorDenumerator;
    }

    public static double calculationNumerator(String recordsNumDen) {

        String[] str = recordsNumDen.split("/");

        int exponent = str[1].length() - factorDenumerator;
        double result = Integer.parseInt(str[0]) / Math.pow(10.0, exponent);

        int factorLenght = factorDenumerator;
        factorLenght = factorLenght + 4;

        if (exponent > 4 && (result > 0 && result < 1)) {
            result = result * Math.pow(10.0, (exponent - 4));

        } else if (factorLenght == str[1].length() && (result > 0 && result < 1)) {
            result = result * 10;
        }


        return result;
    }

    //метод распознавания собственности и аренды, режем по организации, удаляем пробелы для поиска
    public static String rentOrOwnRecord(RegRecordOwner regRecordOwner, String owner) {

        //(||regRecordOwner.getRegRecordEncumbranceList().toString().contains(owner))
        if (owner != null && regRecordOwner.getOwner().replaceAll(" ", "").contains(owner.replaceAll(" ", ""))) {
            regRecordOwner.setTypeOwnShare("собственность");
        } else if (regRecordOwner.getRegRecordEncumbranceList().toString().contains(regRecordOwner.getOwner())) {
            regRecordOwner.setTypeOwnShare("собственность");
        } else if (regRecordOwner.getRegRecordEncumbranceList().toString().toLowerCase().contains("арест")) {
            regRecordOwner.setTypeOwnShare("арест");
        } else if (regRecordOwner.getRegRecordEncumbranceList().toString().toLowerCase().contains("запрещение регистрации")) {
            regRecordOwner.setTypeOwnShare("запрещение регистрации");
        } else if ((!regRecordOwner.getOwner().contains(regRecordOwner.getRegRecordEncumbranceList().toString()))
                && regRecordOwner.getRegRecordEncumbranceList().toString().toLowerCase().contains("аренда")) {
            regRecordOwner.setTypeOwnShare("аренда");
        } else {
            regRecordOwner.setTypeOwnShare("прочее");
        }

        return regRecordOwner.getTypeOwnShare();
    }

    public static String getOwnerStr(RegRecordOwner owner) {
        if (stringOwner == null) {
            try {
                stringOwner = owner.getOwner().substring(owner.getOwner().indexOf("\""), owner.getOwner().lastIndexOf("\"") + 1);
                return stringOwner;

            } catch (StringIndexOutOfBoundsException e) {

            }
        }
        return stringOwner;
    }

    public static void reset() {
        stringOwner = null;
    }

}
