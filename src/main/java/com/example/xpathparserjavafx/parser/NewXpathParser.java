package com.example.xpathparserjavafx.parser;


import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordEncumbrance;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//        String expressionArea = "extract_about_property_land/land_record/params/area/value";
//        String expressionCadCost = "extract_about_property_land/land_record/cost/value";
//        String expressionDealRecord = "extract_about_property_land/deal_records/deal_record";
//        String expressionCadNumber = "extract_about_property_land/land_record/object/common_data/cad_number";

public class NewXpathParser {

    public Cad parseXML(Document document, XPath xPath, Cad cad) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<RegRecordOwner> listOwner = parserOwnerXML(document, xPath, cad);
        List<RegRecordEncumbrance> listEncumbrance = parserEncumbranceXML(document, xPath);

        RegRecordEncumbrance regRecord = null;
        for (int i = 0; i < listEncumbrance.size(); i++) {
            for (int j = 0; j < listEncumbrance.get(i).getCadRegRecordList().size(); j++) {
                regRecord = listEncumbrance.get(i);
                for (int k = 0; k < listOwner.size(); k++) {
                    if (listOwner.get(k).getRegRecord().contains(listEncumbrance.get(i).getCadRegRecordList().get(j))) {
                        listOwner.get(k).getRegRecordEncumbranceList().add(regRecord);
                    }
                }
            }
            regRecord = null;
        }
        cad.setListOwner(listOwner);
        return cad;
    }

    private List<RegRecordOwner> parserOwnerXML(Document document, XPath xPath, Cad cad) throws XPathExpressionException {
        List<RegRecordOwner> listOwner = new ArrayList<>();
        String expression = "extract_about_property_land/right_records/right_record";
        String expressionCadNumber = "extract_about_property_land/land_record/object/common_data/cad_number";
        String expessionArea = "extract_about_property_land/land_record/params/area/value";
        NodeList nodeCad = (NodeList) xPath.compile(expressionCadNumber).evaluate(
                document, XPathConstants.NODESET);
        NodeList nodeArea = (NodeList) xPath.compile(expessionArea).evaluate(document, XPathConstants.NODESET);
        cad.setCadNumber(nodeCad.item(0).getTextContent());
        cad.setArea(nodeArea.item(0).getTextContent());

        NodeList nodeRightList = (NodeList) xPath.compile(expression).evaluate(
                document, XPathConstants.NODESET);

        for (int i = 0; i < nodeRightList.getLength(); i++) {
            Node rightNode = nodeRightList.item(i);
            if (rightNode.getNodeType() == Node.ELEMENT_NODE) {
                RegRecordOwner owner = new RegRecordOwner();
                int countNumber = i + 1;
                owner.setIndexNumber("п. 1." + countNumber);
                owner.setCadNumber(cad.getCadNumber());
                Element elementRight = (Element) rightNode;
                StringBuilder ownerRecord = new StringBuilder("");
                StringBuilder regRecord = new StringBuilder("");
                StringBuilder dataRecord = new StringBuilder("");
                for (int j = 0; j < nodeRightList.item(i).getChildNodes().getLength(); j++) {
                    //дата
                    if (nodeRightList.item(i).getChildNodes().item(j).getNodeName().equals("record_info")) {
                        String[] dateArr = nodeRightList.item(i).getChildNodes().item(j).getTextContent().split("T")[0].split("-");
                        dataRecord.append("от " + dateArr[2] + "." + dateArr[1] + "." + dateArr[0]);
                    } else if (nodeRightList.item(i).getChildNodes().item(j).getNodeName().equals("right_data")) {
                        //тип собственности
                        regRecord.append(nodeRightList.item(i).getChildNodes().item(j)
                                .getChildNodes().item(0).getChildNodes().item(0).getTextContent() + " " +
                                nodeRightList.item(i).getChildNodes().item(j)
                                        .getChildNodes().item(0).getChildNodes().item(1).getTextContent() + ", ");

                        //номер рег записи
                        regRecord.append("№ " + nodeRightList.item(i).getChildNodes().item(1).getChildNodes().item(1).getTextContent() + " " + dataRecord + ", ");
                        owner.setNumberRegistrationRecord(nodeRightList.item(i).getChildNodes().item(1).getChildNodes().item(1).getTextContent());

                        //числитель знаменатель
                        if (nodeRightList.item(i).getChildNodes().item(1).getChildNodes().getLength() > 2) {
                            String numerator = nodeRightList.item(i).getChildNodes().item(1).getChildNodes()
                                    .item(2).getChildNodes().item(0).getChildNodes().item(0).getTextContent();

                            String denumerator = nodeRightList.item(i).getChildNodes().item(1).getChildNodes()
                                    .item(2).getChildNodes().item(0).getChildNodes().item(1).getTextContent();
                            regRecord.append(numerator + "/" + denumerator);
                            owner.setShareNumerator(Double.parseDouble(numerator));
                            owner.setShareDenominator(Double.parseDouble(denumerator));
                            owner.setShare(numerator + "/" + denumerator);
                        } else {
                            //остальные варианты доли
                            owner.setShare(nodeRightList.item(i).getChildNodes().item(1).getChildNodes()
                                    .item(2).getChildNodes().item(0).getTextContent());
                        }
                        owner.setRegRecord(regRecord.toString());
                    } else if (nodeRightList.item(i).getChildNodes().item(j).getNodeName().equals("right_holders")) {
                        //правообладатель
                        for (int k = 0; k < nodeRightList.item(i).getChildNodes().item(j).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().getLength(); k++) {
                            ownerRecord.append(nodeRightList.item(i).getChildNodes().item(j).getChildNodes().item(0).
                                    getChildNodes().item(0).getChildNodes().item(k).getTextContent() + " ");
                        }
                        owner.setOwner(ownerRecord.toString().trim());
                    }
                }
                listOwner.add(owner);
            }
        }
        return listOwner;
    }

    //блок обременение
    private List<RegRecordEncumbrance> parserEncumbranceXML(Document document, XPath xPath) throws XPathExpressionException {

        String expressionEncumbrance = "extract_about_property_land/restrict_records/restrict_record";
        NodeList nodeEncumbranceList = (NodeList) xPath.compile(expressionEncumbrance).evaluate(
                document, XPathConstants.NODESET);
        List<RegRecordEncumbrance> listEncumbrance = new ArrayList<>(nodeEncumbranceList.getLength());

        for (int i = 0; i < nodeEncumbranceList.getLength(); i++) {
            Node encumbranceNode = nodeEncumbranceList.item(i);

            if (encumbranceNode.getNodeType() == Node.ELEMENT_NODE) {
                RegRecordEncumbrance encumbrance = new RegRecordEncumbrance();
                Element elementEncumbrance = (Element) encumbranceNode;
                NodeList nodeChildList = encumbranceNode.getChildNodes();
                StringBuilder docBuilder = new StringBuilder("");

                for (int j = 0; j < nodeChildList.getLength(); j++) {
                    //дата регистрации обременения
                    if (nodeChildList.item(j).getNodeName().equals("record_info")) {
                        encumbrance.setDataRegEncumbrance(nodeChildList.item(j).getTextContent().split("T")[0]);
                    } else if (nodeChildList.item(j).getNodeName().equals("restrictions_encumbrances_data")) {
                        for (int k = 0; k < nodeChildList.item(j).getChildNodes().getLength(); k++) {
                            //рег номер обременения
                            if (nodeChildList.item(j).getChildNodes().item(k).getNodeName().equals("restriction_encumbrance_number")) {
                                encumbrance.setRegNumberEncumbrance(nodeChildList.item(j).getChildNodes().item(k).getTextContent());
                            }
                            //тип обременения
                            else if (nodeChildList.item(j).getChildNodes().item(k).getNodeName().equals("restriction_encumbrance_type")) {
                                encumbrance.setTypeEncumbrance(nodeChildList.item(j).getChildNodes().item(k).getChildNodes().item(0).getTextContent() + " "
                                        + nodeChildList.item(j).getChildNodes().item(k).getChildNodes().item(1).getTextContent());
                            }//срок обременения
                            else if (nodeChildList.item(j).getChildNodes().item(k).getNodeName().equals("period")) {
                                StringBuilder periodBuilder = new StringBuilder("");
                                for (int l = 0; l < nodeChildList.item(j).getChildNodes().item(k).getChildNodes().item(0).getChildNodes().getLength(); l++) {
                                    periodBuilder.append(nodeChildList.item(j).getChildNodes().item(k).getChildNodes().item(0).getChildNodes().item(l).getTextContent() + " ");
                                }
                                encumbrance.setDuration(periodBuilder.toString().trim());
                            }
                            //достаем кадастры из обременения
                            else if (nodeChildList.item(j).getChildNodes().item(k).getNodeName().equals("restricting_rights")) {
                                //item 0 = number - внутренняя техническая запись
                                //item 1 = restrict right -номер регистарции на который идет обременение
                                for (int l = 0; l < nodeChildList.item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
                                    encumbrance.getCadRegRecordList().add(nodeChildList.item(j).getChildNodes().item(k).getChildNodes()
                                            .item(l).getChildNodes().item(1).getTextContent());
                                }
                            }
                        }
                    }//обременитель
                    else if (nodeChildList.item(j).getNodeName().equals("right_holders")) {
                        encumbrance.setEncumbranceOwner(nodeChildList.item(j).getChildNodes().item(0).getChildNodes()
                                .item(0).getChildNodes().item(0).getTextContent());
                    }//реквизиты документа обременения
                    else if (nodeChildList.item(j).getNodeName().equals("underlying_documents")) {
                        for (int k = 0; k < nodeChildList.item(j).getChildNodes().item(0).getChildNodes().getLength(); k++) {
                            docBuilder.append(nodeChildList.item(j).getChildNodes().item(0).getChildNodes().item(k).getTextContent() + " ");

                        }
                        encumbrance.setDocFound(docBuilder.insert(12, " "));
                    }
                }
                listEncumbrance.add(encumbrance);

            }
        }
        return listEncumbrance;
    }
}
