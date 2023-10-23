package com.example.xpathparserjavafx.parser;

import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.FarmRealEstate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmPropertyXpathParser {
    private static final String EXPRESSION_LAND ="extract_rights_individ_available_real_estate_objects/base_data/land_records/land_record";
    private static final String EXPRESSION_ROOM ="extract_rights_individ_available_real_estate_objects/base_data/room_records/room_record";
    private static final String EXPRESSION_BUILD ="extract_rights_individ_available_real_estate_objects/base_data/build_records/build_record";
    private static final String EXPRESSION_CONSTRUCTION = "extract_rights_individ_available_real_estate_objects/base_data/construction_records/construction_record";
    private static final String EXPRESSION_RIGHT_HOLDER = "extract_rights_individ_available_real_estate_objects/details_request/right_holder";

    public Cad parseXml(Document document, XPath xPath, Cad cad) throws XPathExpressionException,SAXException, FileNotFoundException {

        NodeList landList =(NodeList) xPath.compile(EXPRESSION_LAND).evaluate(document, XPathConstants.NODESET);
        NodeList buildList =(NodeList) xPath.compile(EXPRESSION_BUILD).evaluate(document, XPathConstants.NODESET);
        NodeList roomList =(NodeList) xPath.compile(EXPRESSION_ROOM).evaluate(document, XPathConstants.NODESET);
        NodeList сonstructionList =(NodeList) xPath.compile(EXPRESSION_CONSTRUCTION).evaluate(document, XPathConstants.NODESET);

        List<NodeList> allNodeList = new ArrayList<>(Arrays.asList(landList,buildList,roomList,сonstructionList));
        List<FarmRealEstate> farmRealEstateList = new ArrayList<>();

        NodeList rightHolderList = (NodeList) xPath.compile(EXPRESSION_RIGHT_HOLDER).evaluate(document, XPathConstants.NODESET);
        String rightHolder = rightHolderList.item(0).getTextContent().substring(0,rightHolderList.item(0).getTextContent().indexOf(";"));
        rightHolder =rightHolder.replaceAll("\"","");

        cad.setFarm("правообладателю "+ rightHolder);

        for (int i = 0; i < allNodeList.size(); i++) {
            if(allNodeList.get(i).getLength() > 0) {
                nodeListToList(allNodeList.get(i), farmRealEstateList);
            }
        }
        cad.setFarmRealEstateList(farmRealEstateList);
        return cad;
    }

    private void nodeListToList(NodeList mainList, List<FarmRealEstate> farmRealEstateList){
        Node nodeFarmRealEstate = null;
        FarmRealEstate farmRealEstate = null;
        String nodeName = null;
        NodeList nodeList = null;
        StringBuilder stringBuilder = new StringBuilder("");
        StringBuilder stringBuilder1 = new StringBuilder("");

        String stringCut = null;
        String[] strArr = null;
        for (int i = 0; i < mainList.getLength(); i++) {
            nodeFarmRealEstate = mainList.item(i);
            if(nodeFarmRealEstate.getNodeType() == Node.ELEMENT_NODE){
                farmRealEstate = new FarmRealEstate();
                for (int j = 0; j < nodeFarmRealEstate.getChildNodes().getLength(); j++) {
                    //тип недвижимости и кад номер
                    if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("object")){
                        //тип недвижимости, еще вложение child
                        farmRealEstate.setTypeProperty(nodeFarmRealEstate.getChildNodes().item(j)
                                .getChildNodes().item(0)//common_data
                                .getChildNodes().item(0)//type
                                .getChildNodes().item(1).getTextContent());//value
                        //кадастровый номер
                        farmRealEstate.setCadNumber(nodeFarmRealEstate.getChildNodes().item(j)
                                .getChildNodes().item(0)//common_data
                                .getChildNodes().item(1).getTextContent());
                    }//тип использования, назначение объекта недвижимости, площадь
                    else if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("params")){
                        for (int k = 0; k < nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            nodeName = nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getNodeName();
                            if (nodeName.contains("base_parameters")){//параметры длинны или площади в сооружениях, а так же их назначение
                                farmRealEstate.setArea(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getFirstChild().getTextContent());
                                if(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getLastChild() != null){
                                    farmRealEstate.setPurposeOfProperty(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getLastChild().getTextContent());
                                }
                            } else if(nodeName.contains("area")){//площадь
                                farmRealEstate.setArea(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getFirstChild().getTextContent());
                            }else if(nodeName.contains("purpose")){//назначение объекта
                                farmRealEstate.setPurposeOfProperty(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getTextContent().replaceAll("\\d+",""));
                            }else if(nodeName.contains("permitted_uses") ||
                                    nodeName.contains("permitted_use")){//использование объекта
                                farmRealEstate.setPermittedUse(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                            }
                        }
                    }//адрес
                    else if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("address_location")){
                        nodeList =nodeFarmRealEstate.getChildNodes().item(j).getChildNodes();
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            if (nodeList.item(k).getNodeName().equals("address")) {
                                farmRealEstate.setAddress(nodeList.item(k).getLastChild().getTextContent());
                            }
                        }
                    } else if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("address_room")){//адрес помещений
                        nodeList = nodeFarmRealEstate.getChildNodes().item(j).getFirstChild().getChildNodes();
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            if(nodeList.item(k).getNodeName().equals("address")){
                                farmRealEstate.setAddress(nodeList.item(k).getLastChild().getTextContent());

                            }
                        }
                    } else if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("right_record")){//тип собственности, номер регистрации
                        for (int k = 0; k < nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().getLength(); k++) {
                            //фильтр даты
                            //тип собственности, дата, доля
                            if (nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("record_info")){//дата
                                stringCut = nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getTextContent().split("T")[0];
                                strArr = stringCut.split("-");
                                farmRealEstate.setDataReg(strArr[2]+"."+strArr[1]+"."+strArr[0]);
                            } else if(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("right_data")){//инфо о собственности
                                nodeList =nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getChildNodes();
                                for (int l = 0; l < nodeList.getLength(); l++) {
                                    if(nodeList.item(l).getNodeName().contains("right_type")){//тип собственности
                                        stringBuilder.append(nodeList.item(l).getTextContent().replaceAll("\\d+",""));
                                        if(!stringBuilder.toString().contains("Общая долевая собственность")) {
                                            farmRealEstate.setTypeRightAndShare(stringBuilder.toString());
                                        }
                                    }else if(nodeList.item(l).getNodeName().contains("right_number")){//номер регистрации
                                        farmRealEstate.setNumberReg(nodeList.item(l).getTextContent());
                                    }else if(nodeList.item(l).getNodeName().contains("shares")){//доля
                                        stringBuilder.append(", доля в праве "+ nodeList.item(l).getChildNodes().item(0)
                                                .getChildNodes().item(0).getTextContent()
                                                +"/"+
                                                nodeList.item(l).getChildNodes().item(0)
                                                        .getChildNodes().item(1).getTextContent());
                                        farmRealEstate.setTypeRightAndShare(stringBuilder.toString());

                                    }
                                }
                                stringBuilder.setLength(0);
                            }else if(nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getNodeName().equals("underlying_documents")){//документ основание
                                nodeList =  nodeFarmRealEstate.getChildNodes().item(j).getChildNodes().item(k).getChildNodes();
                                stringBuilder.setLength(0);
                                for (int l = 0; l < nodeList.getLength(); l++) {
                                    for (int m = 0; m < nodeList.item(l).getChildNodes().getLength(); m++) {
                                        if(nodeList.item(l).getChildNodes().item(m).getNodeName().equals("document_name")){//имя документа
                                            stringBuilder.append(nodeList.item(l).getChildNodes().item(m).getTextContent()+" ");
                                        }else if(nodeList.item(l).getChildNodes().item(m).getNodeName().equals("document_number")){//номер
                                            stringBuilder.append("№ "+nodeList.item(l).getChildNodes().item(m).getTextContent()+" ");
                                        }else if(nodeList.item(l).getChildNodes().item(m).getNodeName().equals("document_date")){//дата
                                            strArr = nodeList.item(l).getChildNodes().item(m).getTextContent().split("-");
                                            stringBuilder.append("от "+ strArr[2]+"."+strArr[1]+"."+strArr[0]);
                                        }
                                    }
                                    stringBuilder.append(";\n");
                                }
                                farmRealEstate.setDocumentRight(stringBuilder.toString());
                                stringBuilder.setLength(0);
                            }
                        }
                    }else if(nodeFarmRealEstate.getChildNodes().item(j).getNodeName().equals("restrict_records")){//обременения
                        nodeList =nodeFarmRealEstate.getChildNodes().item(j).getChildNodes();//restrict_record
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            for (int l = 0; l < nodeList.item(k).getChildNodes().item(0).getChildNodes().getLength(); l++) {// номер и тип обременения
                                if (nodeList.item(k).getChildNodes().item(0).getChildNodes().item(l).getNodeName().equals("restriction_encumbrance_number")){
                                    stringBuilder.append(nodeList.item(k).getChildNodes().item(0).getChildNodes().item(l).getTextContent()+";\n");
                                }else if(nodeList.item(k).getChildNodes().item(0).getChildNodes().item(l).getNodeName().equals("restriction_encumbrance_type")){
                                    stringBuilder1.append(nodeList.item(k).getChildNodes().item(0).getChildNodes().item(l).getLastChild().getTextContent()+";\n");
                                }
                            }
                        }
                        farmRealEstate.setTypeEncumbrance(stringBuilder1.toString());
                        farmRealEstate.setNumberRegistrationEncumbrance(stringBuilder.toString());
                        stringBuilder1.setLength(0);
                        stringBuilder.setLength(0);
                    }
                }
            }
            farmRealEstateList.add(farmRealEstate);
        }

    }
}
