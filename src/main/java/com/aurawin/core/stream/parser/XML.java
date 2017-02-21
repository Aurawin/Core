package com.aurawin.core.stream.parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.aurawin.core.stream.MemoryStream;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

public class XML {

    public static Document parseXML(MemoryStream Data) {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document result = null;
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException pe){
            return result;
        }
        InputStream is = Channels.newInputStream(Data);
        try {
            result = builder.parse(is);
        } catch (IOException ie){
            return result;
        } catch (SAXException se) {
            return result;
        }
        return result;
    }
}
