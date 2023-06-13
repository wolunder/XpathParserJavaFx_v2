package com.example.xpathparserjavafx.parser;

import com.example.xpathparserjavafx.exception.ParserFormatException;
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

public class OldXpathParser {

    public Cad parseXML(Document document, XPath xPath, Cad cad) throws ParserConfigurationException, IOException, NullPointerException,SAXException, XPathExpressionException, ParserFormatException {
        String expressionCadNumber = "KVZU/Parcels/Parcel";
        String expression1 = "extract_about_property_land/land_record/object/common_data/cad_number";
        NodeList nodeCadNumList = (NodeList) xPath.compile(expressionCadNumber).evaluate(
                document, XPathConstants.NODESET);

        Element element = (Element) nodeCadNumList.item(0);
        cad.setCadNumber(element.getAttribute("CadastralNumber"));
        //площадь участка
        String expressionArea = "KVZU/Parcels/Parcel/Area/Area";
        NodeList nodeAreaList = (NodeList) xPath.compile(expressionArea).evaluate(
                document, XPathConstants.NODESET);
        cad.setArea(nodeAreaList.item(0).getTextContent());

        String expressionExtractObject = "KVZU/ReestrExtract/ExtractObjectRight//ObjectRight//Right";
        NodeList nodeExtractObjectList = (NodeList) xPath.compile(expressionExtractObject).evaluate(
                document, XPathConstants.NODESET);
        List<RegRecordOwner> listOwner = new ArrayList<>();
        for (int i = 1; i < nodeExtractObjectList.getLength(); i++) {
            if (nodeExtractObjectList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                RegRecordOwner owner = new RegRecordOwner();
                RegRecordEncumbrance encumbrance = new RegRecordEncumbrance();
                int count = i + 1;
                owner.setIndexNumber("п. 1." + count);
                Node nNode = nodeExtractObjectList.item(i);
                for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
                    //собственник доли, фильтрация полей
                    if (nNode.getChildNodes().item(j).getNodeName().equals("Owner")) {
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Person")
                                    || nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Organization")) {
                                owner.setOwner(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent().split("\n")[2]);
                            }
                        }
                    } else if (nNode.getChildNodes().item(j).getNodeName().equals("NoOwner")) {
                        owner.setOwner(nNode.getChildNodes().item(j).getTextContent());
                    } else if (nNode.getChildNodes().item(j).getNodeName().equals("Registration")) {
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            String strType = "";
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("RegNumber")) {
                                //чистый номер регистрации
                                owner.setNumberRegistrationRecord(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            } else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Type")) {
                                strType = nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent();

                            }//запись с регистрацией долей и датой
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Name")) {
                                owner.setRegRecord(strType + ", " + nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            }//доля
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("ShareText")) {
                                owner.setShare(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                                if (nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent().contains("/")) {
                                    String[] shareArr = nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent().split("/");
                                    if (shareArr.length == 2) {
                                        owner.setShareNumerator(Double.parseDouble(shareArr[0]));
                                        owner.setShareDenominator(Double.parseDouble(shareArr[1]));
                                    }
                                }
                            }
                        }
                    }
                    //обременение
                    else if (nNode.getChildNodes().item(j).getNodeName().equals("Encumbrance")) {
                        for (int k = 0; k < nNode.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("RegNumber")) {
                                encumbrance.setRegNumberEncumbrance(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            } else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Name")) {
                                encumbrance.setTypeEncumbrance(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            } else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("RegDate")) {
                                encumbrance.setDataRegEncumbrance(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            }//срок обременения
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Duration")) {
                                StringBuilder durationBuilder = new StringBuilder("");
                                for (int l = 0; l < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
                                    if (!nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getTextContent().isEmpty()) {
                                        durationBuilder.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getTextContent() + " ");
                                    }
                                }
                                encumbrance.setDuration(durationBuilder.toString().replaceAll("\n", "").trim());
                            }//обременитель
                            else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("Owner")) {
                                encumbrance.setEncumbranceOwner(nNode.getChildNodes().item(j).getChildNodes().item(k).getTextContent().split("\n")[4]);
                            } else if (nNode.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("DocFound")) {
                                StringBuilder docFoundBuilder = new StringBuilder("");
                                for (int l = 0; l < nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
                                    docFoundBuilder.append(nNode.getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getTextContent().replaceAll("\n", " "));
                                }
                                encumbrance.setDocFound(docFoundBuilder);
                            }
                        }
                        owner.getRegRecordEncumbranceList().add(encumbrance);
                    } else if (nNode.getChildNodes().item(j).getNodeName().equals("NoEncumbrance")) {
                        encumbrance.setRegNumberEncumbrance(nNode.getChildNodes().item(j).getTextContent());
                        owner.getRegRecordEncumbranceList().add(encumbrance);
                    }
                }
                listOwner.add(owner);
            }
        }
        cad.setListOwner(listOwner);
        return cad;
    }
}
