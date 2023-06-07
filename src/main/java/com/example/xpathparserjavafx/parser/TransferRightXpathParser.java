package com.example.xpathparserjavafx.parser;


import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import org.w3c.dom.Document;
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

public class TransferRightXpathParser {


    public Cad parseXML(Document document, XPath xPath, Cad cad) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        cad.setTransfer(true);

        String expression = "extract_transfer_rights_property/right_records//right_record";
        String expressionCadNumberTransfer = "extract_transfer_rights_property/base_data/land_record/object/common_data/cad_number";

        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
        NodeList nodeCadum = (NodeList) xPath.compile(expressionCadNumberTransfer).evaluate(document, XPathConstants.NODESET);
        cad.setCadNumber(nodeCadum.item(0).getTextContent());

        List<RegRecordOwner> listOwner = new ArrayList<>(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node rightNode = nodeList.item(i);

            if (rightNode.getNodeType() == Node.ELEMENT_NODE) {
                RegRecordOwner owner = new RegRecordOwner();
                int countNumber = i + 1;
                Node nNode = nodeList.item(i);
                owner.setIndexNumber("п. " + countNumber);
                StringBuilder dataRecord = new StringBuilder("");
                StringBuilder regRecord = new StringBuilder("");
                for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
                    //дата
                    if (nNode.getChildNodes().item(j).getNodeName().equals("record_info")) {
                        String[] dateArr = nNode.getChildNodes().item(j).getTextContent().split("T")[0].split("-");
                        dataRecord.append("от " + dateArr[2] + "." + dateArr[1] + "." + dateArr[0]);
                    }// рег запись
                    else if (nNode.getChildNodes().item(j).getNodeName().equals("right_data")) {
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            //тип собственности
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("right_type")) {
                                regRecord.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(0).getTextContent() + " " +
                                        nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(1).getTextContent() + ", ");
                            }//номер регистрации
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("right_number")) {
                                regRecord.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent() + " " + dataRecord + ", ");
                                owner.setNumberRegistrationRecord(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            }//доля
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("shares")) {
                                if (nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(0).getChildNodes().getLength() == 2) {
                                    regRecord.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes()
                                            .item(0).getChildNodes().item(0).getTextContent() + "/"
                                            + nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes()
                                            .item(0).getChildNodes().item(1).getTextContent());
                                } else {
                                    regRecord.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                                }
                            }
                        }
                        owner.setRegRecord(regRecord.toString());
                    }//правообладатель
                    else if (nNode.getChildNodes().item(j).getNodeName().equals("right_holders")) {
                        //физ лицо
                        if (nNode.getChildNodes().item(j).getChildNodes().item(0).getChildNodes().item(0).getNodeName().equals("individual")) {
                            owner.setOwner(nNode.getChildNodes().item(j).getChildNodes().item(0).getChildNodes().item(0).getTextContent());
                        }//юр лицо
                        else {
                            StringBuilder ownerBuilder = new StringBuilder("");
                            for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().item(0)
                                    .getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().getLength(); k++) {
                                if (k <= 1) {
                                    ownerBuilder.append(nNode.getChildNodes().item(j).getChildNodes().item(0)
                                            .getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(k).getTextContent() + " ");
                                }
                            }
                            owner.setOwner(ownerBuilder.toString());
                        }
                    }//документы права собственности собственника
                    else if (nNode.getChildNodes().item(j).getNodeName().equals("underlying_documents")) {
                        StringBuilder docBuilder = new StringBuilder("");
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            for (int l = 0; l < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
                                if (nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("document_code")) {
                                    for (int m = 0; m < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getChildNodes().getLength(); m++) {
                                        docBuilder.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes()
                                                .item(l).getChildNodes().item(m).getTextContent() + " ");
                                    }
                                } else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("document_name")) {
                                    docBuilder.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getTextContent() + ";\n");
                                }
                            }
                        }
                        owner.setUnderlyingDocument(docBuilder);
                    }//документ прекращения предыдущего права
                    else if (nNode.getChildNodes().item(j).getNodeName().equals("changes")) {
                        StringBuilder docBuilder1 = new StringBuilder("");
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("fixed_at")) {
                                owner.setCancelData(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent().split("T")[0]);
                            }
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("underlying_documents")) {
                                for (int l = 0; l < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {

                                    //документы перехода
                                    if (nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("underlying_document")) {
                                        for (int m = 0; m < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getChildNodes().getLength(); m++) {

                                            if (m == 0 && nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).
                                                    getChildNodes().item(0).getChildNodes().getLength() > 1) {

                                                docBuilder1.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).
                                                        getChildNodes().item(m).getChildNodes().item(0).getTextContent() + " " +
                                                        nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).
                                                                getChildNodes().item(m).getChildNodes().item(1).getTextContent() + ", ");
                                            } else {
                                                docBuilder1.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getChildNodes().item(m).getTextContent() + " ");
                                            }
                                        }

                                    }
                                }
                                owner.getChangeUnderlyingDocuments().append(docBuilder1.toString().trim() + ";\n");
                            }

                        }
                    }
                }
                listOwner.add(owner);
            }
        }
        cad.setListOwner(listOwner);
        return cad;
    }
}

