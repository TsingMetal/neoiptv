package com.tsing.iptv;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.util.List;

/** parse xml string returned from sbt */
public class IptvXmlParser implements XmlParser {
  
  private Document document;
  
  /** initialize document */
  public IptvXmlParser() {
  }

  /** initialize document here */
  public void parse(String strXML) {
    try {
      document = DocumentHelper.parseText(strXML);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /** return a node's text content by giving the node's tag name */
  public String getValue(String tag) {
    Element root = document.getRootElement();

    List<Element> nodes = root.elements();

    // traverse all the nodes, get the wanted one:
    for (Element node : nodes) {
      if (node.getName() == tag)
        return node.getText();
    }
    return null;
  }

  /*//~ for debugging
  public static void main(String[] args) {
    String strXML = String.format(CmdXml.SETXML, "123", "abc", "456", "de");
    IptvXmlParser parser = new IptvXmlParser(strXML);
    System.out.println("cmd=" + parser.getValue("cmd"));
    System.out.println("mac=" + parser.getValue("mac"));
    System.out.println("sn=" + parser.getValue("sn"));
    System.out.println("hdcp_key=" + parser.getValue("hdcp_key"));
  }
  *///~ tested OK
}
