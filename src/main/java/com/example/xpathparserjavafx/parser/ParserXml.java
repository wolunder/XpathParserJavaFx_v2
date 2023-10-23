package com.example.xpathparserjavafx.parser;


import com.example.xpathparserjavafx.exception.ParserFormatException;
import com.example.xpathparserjavafx.model.Cad;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class ParserXml implements ParserFile {
    private final NewXpathParser newXpathParser;
    private final OldXpathParser oldXpathParser;
    private final TransferRightXpathParser transferParser;
    private final FarmPropertyXpathParser farmPropertyXpathParser;

    public ParserXml() {
        this.newXpathParser = new NewXpathParser();
        this.oldXpathParser = new OldXpathParser();
        this.transferParser = new TransferRightXpathParser();
        this.farmPropertyXpathParser = new FarmPropertyXpathParser();
    }

    @Override
    public Cad parseFile(File fileXML) throws IOException,NullPointerException, SAXException, ParserConfigurationException, XPathExpressionException, ParserFormatException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(fileXML);
        document.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String startWithDoc = document.getDocumentElement().toString();
        Cad cad = new Cad();
        //выбор парсера по типу выписки
        if (startWithDoc.contains("KVZU")) {
            cad = oldXpathParser.parseXML(document, xPath, cad);
        } else if (startWithDoc.contains("extract_about_property_land") ||startWithDoc.contains("extract_base_params_land") ) {
            cad = newXpathParser.parseXML(document, xPath, cad);
        } else if (startWithDoc.contains("extract_transfer_rights_property") ) {
            cad = transferParser.parseXML(document, xPath, cad);
        } else if(startWithDoc.contains("extract_rights_individ_available_real_estate_objects")){
            cad = farmPropertyXpathParser.parseXml(document,xPath,cad);
        }
        else {
            throw new ParserFormatException();
        }
        return cad;
    }
}
