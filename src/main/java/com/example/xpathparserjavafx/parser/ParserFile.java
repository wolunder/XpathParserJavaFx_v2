package com.example.xpathparserjavafx.parser;

import com.example.xpathparserjavafx.exception.ParserFormatException;
import com.example.xpathparserjavafx.model.Cad;

import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;


public interface ParserFile {
    Cad parseFile(File file) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParserFormatException;
}
