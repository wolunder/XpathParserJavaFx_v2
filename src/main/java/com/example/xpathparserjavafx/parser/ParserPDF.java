package com.example.xpathparserjavafx.parser;


import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordEncumbrance;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserPDF implements ParserFile {

    @Override
    public Cad parseFile(File file) throws IOException, FileNotFoundException, OutOfMemoryError {
        LabelStringPDF labelStringPDF = new LabelStringPDF();
        PDDocument pdDocument = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        String text = textStripper.getText(pdDocument);
        pdDocument.close();
        System.out.println(text);
//        try {
//            docCreate(text);
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        Cad cad = new Cad();
        String subArea = null;

        if(text.contains("Площадь:")){
            subArea = text.substring(text.indexOf("Площадь:"), text.indexOf("Кадастровая стоимость,"));
        }else if(text.contains("Площадь, м2:")){
            subArea = text.substring(text.indexOf("Площадь, м2:"), text.indexOf("Кадастровая стоимость,"));
        }

//        String subArea = text.substring(text.indexOf("Площадь:"), text.indexOf("Кадастровая стоимость,"));
        if (subArea.contains("+/-")) {
            subArea = subArea.substring(0, subArea.indexOf("+/-"));
        }

        subArea = replaceNewlineCharacters(subArea.replace("Площадь: ", "")).trim();
        cad.setArea(subArea);

//        String subCost = text.substring(text.indexOf("Кадастровая стоимость, руб.:"),
//                text.indexOf("Кадастровые номера расположенных в пределах земельного"));
        String subCadNum = text.substring(text.indexOf("Кадастровый номер:"), text.indexOf("Номер кадастрового квартала:"));
        subCadNum = replaceNewlineCharacters(subCadNum.replace("Кадастровый номер: ", "")).trim();
        if (subCadNum.contains("(Единое землепользование)")) {
            subCadNum = subCadNum.replace("(Единое землепользование)", "");
        }
        cad.setCadNumber(subCadNum);
//        System.out.println(subCadNum);

        if (text.contains("Правообладатель (правообладатели):")) {
            String subOwner1 = text.substring(text.indexOf("Правообладатель (правообладатели):"),
                    text.lastIndexOf("5 Договоры участия в долевом строительстве: не зарегистрировано"));
            String[] allStrArr = subOwner1.split("Правообладатель ");
            text = null;
            List<RegRecordOwner> listOwner = new ArrayList<>();
            for (String s : allStrArr) {
                if (s.startsWith("(правообладатели)")) {
                    RegRecordOwner owner = new RegRecordOwner();
                    //вырезание печати
                    if (s.contains("полное наименование должности подпись инициалы, фамилия")) {
                        String[] strArr = s.split("полное наименование должности подпись инициалы, фамилия");
                        StringBuilder stringBuilder = new StringBuilder("");
                        for (String str : strArr) {
                            String[] array = str.split("Действителен:");
                            for (String sArr : array) {
                                if (!sArr.contains("М.П.")) {
                                    stringBuilder.append(sArr + " ");
                                    if (sArr.startsWith(" c ")) {
                                        stringBuilder.append(sArr.indexOf("\n") + " ");
                                    }
                                }
                            }
                            s = stringBuilder.toString();
                        }
                    }
                    //извлечение данных из строк документа
                    //правобладатель
                    String[] splitArray = null;
                    StringBuilder splitBuilder = new StringBuilder("");
                    String subOwner = s.substring(s.indexOf(labelStringPDF.getBeginLabelOwner()),
                            s.indexOf(labelStringPDF.getEndLabelOwner()));
                    subOwner = subOwner.substring(subOwner.indexOf(":") + 1).trim();
                    splitArray = subOwner.split(" ", 2);
                    owner.setIndexNumber("п." + splitArray[0]);
                    if (splitArray[1].contains(" по ")) {
                        String ifString = ParserPDF.replaceNewlineCharacters(splitArray[1]);
                        splitArray[1] = ifString.substring(0, ifString.indexOf("  "));
                    }
                    owner.setOwner(ParserPDF.replaceNewlineCharacters(splitArray[1]));

                    //рег запись
                    String subRegRecord = s.substring(s.indexOf(labelStringPDF.getBeginLabelRegRecord()),
                            s.indexOf(labelStringPDF.getEndLabelRegRecord()));
                    subRegRecord = subRegRecord.substring(subRegRecord.indexOf(":") + 1).trim();
                    splitArray = new String[3];
                    String[] subArr = subRegRecord.replaceAll("\\p{Cntrl}", "&").split("&");
                    for (int i = 0; i < subArr.length; i++) {
                        if (!subArr[i].isEmpty()) {
                            splitBuilder.append(subArr[i] + ",");
                        }
                    }//0 - тип собств, 1- доля, 2- рег запись - общедолевая, 3- дата, 0 - тип собств, 1- рег запись, 2- дата - собственность
                    splitArray = splitBuilder.toString().split(",");
                    if (splitArray.length == 4) {
                        splitBuilder = new StringBuilder("");
                        System.out.println(splitArray[0]);
                        splitBuilder.append("001002000000 "+splitArray[0].substring(splitArray[0].indexOf(" ")) + ", " +
                                splitArray[2] + " от " + splitArray[3].substring(0, splitArray[3].indexOf(" ")) + ", " +
                                splitArray[1].trim());
                        System.out.println( "******* "+splitBuilder);
                        owner.setRegRecord(splitBuilder.toString().trim());
                        owner.setNumberRegistrationRecord(splitArray[2]);
                        // доли
                        String share = splitArray[1].trim();
                        if (share.contains("/")) {
                            if (share.contains(" ")) {
                                share = share.substring(0, share.indexOf(" "));
                            }
                            String[] shareAr = share.split("/");
                            owner.setShareNumerator(Double.parseDouble(shareAr[0]));
                            owner.setShareDenominator(Double.parseDouble(shareAr[1]));
                        } else {
                            owner.setShareNumerator(0.0);
                            owner.setShareDenominator(0.0);
                        }
                        owner.setShare(splitArray[1]);
                    }else if(splitArray.length == 3){
                        String record = splitBuilder.toString();
                        record = record.substring(record.indexOf(" ")+1, record.lastIndexOf(" ")).replaceAll(",",", ");
                        owner.setRegRecord(record);
                        owner.setNumberRegistrationRecord(splitArray[1]);
                    }
                    else {
                        owner.setShareNumerator(0.0);
                        owner.setShareDenominator(0.0);
                    }
                    // обременение
                    String subEncumbrance = s.substring(s.indexOf(labelStringPDF.getEndLabelEncumbrance()));
                    String[] arrEncumbrance = subEncumbrance.split("вид:");

                    for (String e : arrEncumbrance) {
                        //4 Ограничение прав и обременение объекта недвижимости:
                        if (!e.contains("Ограничение прав и обременение объекта недвижимости:")) {
                            RegRecordEncumbrance regRecordEncumbrance = new RegRecordEncumbrance();
                            //тип аренды
                            String subTypeEncum = e.substring(0, e.indexOf("\n"));
                            regRecordEncumbrance.setTypeEncumbrance(subTypeEncum.replaceAll("\\p{Cntrl}", "").trim());
                            //дата рег запись
                            String subData = e.substring(e.indexOf(labelStringPDF.getBeginLabelData()),
                                    e.indexOf(labelStringPDF.getEndLabelData()));
                            regRecordEncumbrance.setDataRegEncumbrance(subData.substring(subData.indexOf(":") + 1).trim().split(" ")[0]);
                            //рег запись
                            String subRegEncum = e.substring(e.indexOf(labelStringPDF.getEndLabelData()),
                                    e.indexOf(labelStringPDF.getEndLabelRegEncum()));
                            if (subRegEncum.contains(" по ")) {
                                String ifString = ParserPDF.replaceNewlineCharacters(subRegEncum);
                                subRegEncum = ifString.substring(0, ifString.indexOf("  "));
                            }
                            regRecordEncumbrance.setRegNumberEncumbrance(subRegEncum.substring(subRegEncum.indexOf(":") + 1).trim());

                            //срок обременения
                            String subDuration = e.substring(e.indexOf(labelStringPDF.getBeginLabelDuration()),
                                    e.indexOf(labelStringPDF.getEndLabelDuration()));
                            regRecordEncumbrance.setDuration(subDuration.replaceAll("\\p{Cntrl}", "&").split("&&")[1]);
                            //обременитель
                            String subOwnerEncum = e.substring(e.indexOf(labelStringPDF.getBeginLabelEncumOwner()),
                                    e.indexOf(labelStringPDF.getEndLabelEncumOwner()));
                            regRecordEncumbrance.setEncumbranceOwner(subOwnerEncum.replaceAll("\\p{Cntrl}", "&").split("&&")[1]);

                            String subDocFound = e.substring(e.indexOf(labelStringPDF.getBeginLabelDocFound()),
                                    e.indexOf(labelStringPDF.getEndLabelDocFound()));
                            //вырезание заголовка таблиц
                            String strDoc = subDocFound.substring(subDocFound.indexOf(":") + 1);
                            if (strDoc.contains("Земельный участок")) {
                                StringBuilder docBuilder = new StringBuilder("");
                                String[] testStr = strDoc.replaceAll("\\p{Cntrl}", "&").split("&&");
                                for (int i = 0; i < testStr.length; i++) {
                                    if (testStr[i].equals("Земельный участок")) {
                                        i = i + 5;
                                    }
                                    docBuilder.append(testStr[i] + "\n");
                                }
                                strDoc = docBuilder.toString();
                            }
                            if (strDoc.contains(" по ")) {
                                String ifString = ParserPDF.replaceNewlineCharacters(strDoc);
                                strDoc = ifString.substring(0, ifString.indexOf("  "));

                            } else {
                                strDoc = ParserPDF.replaceNewlineCharacters(strDoc);
                            }
                            regRecordEncumbrance.setDocFound(new StringBuilder(strDoc));
                            owner.getRegRecordEncumbranceList().add(regRecordEncumbrance);
                        }
                    }
                    listOwner.add(owner);
                }
            }


            allStrArr = null;
            cad.setListOwner(listOwner);
        }
        return cad;
    }

    private static String replaceNewlineCharacters(String str) {
        String[] strArr = str.replaceAll("\\p{Cntrl}", "&").split("&&");
        StringBuilder stringBuilder = new StringBuilder("");
        for (String s : strArr) {
            stringBuilder.append(s + " ");
        }
        return stringBuilder.toString().trim();
    }

    private void docCreate(String text) throws IOException {
        String filename = "C:\\test.txt";
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(text.getBytes());
        fos.flush();
        fos.close();

    }
}

