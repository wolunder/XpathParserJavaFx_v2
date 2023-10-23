package com.example.xpathparserjavafx.parser;

import com.example.xpathparserjavafx.exception.ParserFormatException;
import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.FarmRealEstate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FarmRealEstateParserPDF implements ParserFile {
    private FarmRealEstateLabel label = new FarmRealEstateLabel();
    private StringBuilder strTypeProperty = new StringBuilder("");
    private StringBuilder strCadNumber = new StringBuilder("");
    private StringBuilder strPurposeOfProperty = new StringBuilder("");
    private StringBuilder strPermittedUse = new StringBuilder("");
    private StringBuilder strAddress = new StringBuilder("");
    private StringBuilder strArea = new StringBuilder("");
    private StringBuilder strRightOrShare = new StringBuilder("");
    private String address = new String();
    private StringBuilder strDataReg = new StringBuilder("");
    private StringBuilder strNumberReg = new StringBuilder("");
    private StringBuilder strDocRight = new StringBuilder("");
    private StringBuilder strCancelRight = new StringBuilder("");
    private StringBuilder strCutSignet = new StringBuilder("");
    private StringBuilder strCutString = new StringBuilder("");
    private StringBuilder strEncumRight = new StringBuilder("");
    private StringBuilder strEncumNumber = new StringBuilder("");

    private String cutString = "";
    private String[] strArr = null;
    private String[] encumArr = null;
    private FarmRealEstate farmRealEstate = null;

    @Override
    public Cad parseFile(File file) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParserFormatException {

        PDDocument pdDocument = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        String text = textStripper.getText(pdDocument);
        pdDocument.close();
        String[] arr = text.split(FarmRealEstateLabel.OBJECT_CUTTER);

        List<FarmRealEstate> farmPropertiesList = new ArrayList<>(arr.length);
        Cad cad = new Cad();
        String nameFarm = arr[0].substring(arr[0].indexOf("правообладателю"),arr[0].indexOf("; ИНН:"));
        nameFarm = InterpretationRecords.replaceNewlineCharacters(nameFarm).replaceAll("\n","").replaceAll("\"","");
        cad.setFarm(nameFarm);

        for (int i = 1; i < arr.length; i++) {
            farmRealEstate = new FarmRealEstate();
            strTypeProperty.append(arr[i].substring(0, arr[i].indexOf("Кадастровый номер:")).trim());
            farmRealEstate.setTypeProperty(strTypeProperty.toString());
            strTypeProperty.setLength(0);

            if (arr[i].contains("полное наименование должности подпись инициалы, фамилия")) {
                arr[i] = InterpretationRecords.cutSignet(arr[i], strCutSignet);
            }
            //кад номер
            strCadNumber.append(arr[i].substring(arr[i].indexOf("Кадастровый номер:"), arr[i].indexOf("Назначение объекта недвижимости:")).trim());
            farmRealEstate.setCadNumber(strCadNumber.toString().split("номер:")[1].trim());
            strCadNumber.setLength(0);
            //назначения использование
            strPurposeOfProperty.append(arr[i].substring(arr[i].indexOf("Назначение объекта недвижимости:"),
                    arr[i].indexOf("Виды разрешенного использования объекта недвижимости:")).trim());
            farmRealEstate.setPurposeOfProperty(strPurposeOfProperty.toString().split("недвижимости:")[1].trim());
            strPurposeOfProperty.setLength(0);
            //адрес
            if (arr[i].contains(FarmRealEstateLabel.LOCATION)) {
                strPermittedUse.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.PERMITTED_USE),
                        arr[i].indexOf(FarmRealEstateLabel.LOCATION)).trim());
                strAddress.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.LOCATION),
                        arr[i].indexOf(FarmRealEstateLabel.AREA)).trim());
                address = strAddress.toString().split(FarmRealEstateLabel.LOCATION)[1].trim();
            } else if (arr[i].contains(FarmRealEstateLabel.ADDRESS)) {
                strPermittedUse.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.PERMITTED_USE),
                        arr[i].indexOf(FarmRealEstateLabel.ADDRESS)).trim());
                strAddress.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.ADDRESS),
                        arr[i].indexOf(FarmRealEstateLabel.AREA)).trim());
                address = strAddress.toString().split(FarmRealEstateLabel.ADDRESS)[1].trim();
            }

            farmRealEstate.setAddress(InterpretationRecords.replaceNewlineCharacters(address));//удвляем мусорные знаки
            farmRealEstate.setPermittedUse(strPermittedUse.toString().split("недвижимости:")[1].trim());
            //площадь
            strArea.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.AREA) + FarmRealEstateLabel.AREA_LENGTH,
                    arr[i].indexOf(FarmRealEstateLabel.TYPE_RIGHT_AND_SHARE)).trim());
            cutString = strArea.toString().split(" ")[0];
            if (!cutString.contains("\n")) {
                farmRealEstate.setArea(cutString);
            } else {
                farmRealEstate.setArea(InterpretationRecords.replaceNewlineCharacters(cutString).replaceAll("\n", " ").split(" ")[0]);
            }
            //доля или собственность
            strRightOrShare.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.TYPE_RIGHT_AND_SHARE),
                    arr[i].indexOf(FarmRealEstateLabel.DATA_REGISTRATION)));
            farmRealEstate.setTypeRightAndShare(InterpretationRecords.replaceNewlineCharacters(
                    strRightOrShare.toString().split(FarmRealEstateLabel.TYPE_RIGHT_AND_SHARE)[1].trim()));
            //дата регистрации
            strDataReg.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.DATA_REGISTRATION),
                    arr[i].indexOf(FarmRealEstateLabel.NUMBER_REGISTRATION)));
            farmRealEstate.setDataReg(strDataReg.toString().split(FarmRealEstateLabel.DATA_REGISTRATION)[1].trim());
            //номер регистрации
            strNumberReg.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.NUMBER_REGISTRATION),
                    arr[i].indexOf(FarmRealEstateLabel.DOCUMENT_RIGHT)));
            farmRealEstate.setNumberReg(strNumberReg.toString().split(FarmRealEstateLabel.NUMBER_REGISTRATION)[1].trim());
            //документ основание
            strDocRight.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.DOCUMENT_RIGHT),
                    arr[i].indexOf(FarmRealEstateLabel.CANCEL_RIGHT)));
            farmRealEstate.setDocumentRight(InterpretationRecords.replaceNewlineCharacters(strDocRight.toString().split(FarmRealEstateLabel.DOCUMENT_RIGHT)[1].trim()));
            //дата прекращения и обременение
            if (arr[i].contains(FarmRealEstateLabel.ENCUMBRANCE_RIGHT_ON)) {
                strCancelRight.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.CANCEL_RIGHT),
                        arr[i].indexOf(FarmRealEstateLabel.ENCUMBRANCE_RIGHT_ON)));
                if (strCancelRight.toString().contains("данные отсутствуют")) {
                    farmRealEstate.setCancelRight("данные отсутствуют");
                } else {
                    farmRealEstate.setCancelRight(strCancelRight.toString());
                }
                cutString = arr[i].split(FarmRealEstateLabel.ENCUMBRANCE_RIGHT_ON)[1];
                if (cutString.contains("номер государственной регистрации:")) {//фильтруем тип и регистрацию ограничения
                    if (cutString.contains("полное наименование должности подпись инициалы, фамилия")) {
                        cutString = InterpretationRecords.cutSignet(cutString, strCutSignet);
                    }
                    strArr =cutString.split("вид:");

                    for (String s: strArr){
                        if(s.contains(":")) {
                            encumArr = InterpretationRecords.replaceNewlineCharacters(s).split(" номер государственной регистрации:");
                            strEncumRight.append("- "+encumArr[0].trim()+";\n");
                            strEncumNumber.append("- "+encumArr[1].trim().split(" ")[0]+";\n");
                        }
                    }
                    farmRealEstate.setTypeEncumbrance(strEncumRight.toString());
                    farmRealEstate.setNumberRegistrationEncumbrance(strEncumNumber.toString());
                    strCutString.setLength(0);
                    strEncumRight.setLength(0);
                    strEncumNumber.setLength(0);
                }
            } else if (arr[i].contains(FarmRealEstateLabel.ENCUMBRANCE_RIGHT)) {
                strCancelRight.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.CANCEL_RIGHT),
                        arr[i].indexOf(FarmRealEstateLabel.ENCUMBRANCE_RIGHT)));
                cutString = InterpretationRecords.replaceNewlineCharacters(strCancelRight.toString());
                farmRealEstate.setCancelRight(cutString.replace("дата государственной регистрации прекращения права:", "").replaceAll("\\d+", "").trim());
                strEncumRight.append(arr[i].substring(arr[i].indexOf(FarmRealEstateLabel.ENCUMBRANCE_RIGHT)));
                cutString = strEncumRight.toString().split("\n")[0];
                farmRealEstate.setEncumbranceRight(cutString.substring(cutString.indexOf(":") + 1).trim());
            }
            strPermittedUse.setLength(0);
            strAddress.setLength(0);
            strArea.setLength(0);
            strRightOrShare.setLength(0);
            strDataReg.setLength(0);
            strNumberReg.setLength(0);
            strCancelRight.setLength(0);
            strEncumRight.setLength(0);
            strDocRight.setLength(0);
            farmPropertiesList.add(farmRealEstate);
        }
        cad.setFarmRealEstateList(farmPropertiesList);
        return cad;
    }
}
