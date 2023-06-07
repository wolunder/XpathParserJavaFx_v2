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

    public ParserXml() {
        this.newXpathParser = new NewXpathParser();
        this.oldXpathParser = new OldXpathParser();
        this.transferParser = new TransferRightXpathParser();
    }

    @Override
    public Cad parseFile(File fileXML) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParserFormatException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(fileXML);
        document.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String endWithDoc = document.getDocumentElement().toString();
        Cad cad = new Cad();
        //выбор парсера по типу выписки
        if (endWithDoc.contains("KVZU")) {
            cad = oldXpathParser.parseXML(document, xPath, cad);
        } else if (endWithDoc.contains("extract_about_property_land")) {
            cad = newXpathParser.parseXML(document, xPath, cad);
        } else if (endWithDoc.contains("extract_transfer_rights_property")) {
            cad = transferParser.parseXML(document, xPath, cad);
        } else {
            throw new ParserFormatException();
        }
        return cad;
    }
}
