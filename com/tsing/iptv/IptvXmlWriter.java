package com.tsing.iptv;

import java.util.Date;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

/** 
 * write XML string to file
 * debugged OK; date: Sun 20 Nov 19:16:09 CST 2016
 */
public class IptvXmlWriter implements XmlWriter {
  private String logPath = "IPTV/LOG/PASS/";
  private String failLogPath = "IPTV/LOG/FAIL/";

  public Document createXML(LinkedHashMap<String, String> map) {
    Document document = DocumentHelper.createDocument();
    Element root = document.addElement("test_info");

    for (Object object : map.keySet().toArray()) {
      String key = (String)object;
      Element element = root.addElement(key);
      element.addText(map.get(key));
    }
    
    SimpleDateFormat format = 
      new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
    String timeStamp = format.format(new Date());
    Element element = root.addElement("date");
    element.addText(timeStamp);

    return document;
  }

	public void write(LinkedHashMap<String, String> map) {
    // generate path by yy-mm-date
    SimpleDateFormat pathFormat = 
      new SimpleDateFormat("yyyy-MM-dd/");
    String datePath = pathFormat.format(new Date());

    // generate filename: etc 20161120162400#sn.xml
    SimpleDateFormat fileFormat = 
      new SimpleDateFormat("yyyyMMddHHmmss");
    String filename = 
      fileFormat.format(new Date()) + "#" + map.get("sn") + ".xml";

    if (map.get("status").equals("FAIL")) 
      logPath = failLogPath;

    String absFilename = // absolute file path
      logPath + datePath + filename;
    File file = new File(absFilename);

    Document document = createXML(map);
    printXML(document); // for debugging

    XMLWriter out = null;
    try { // suppose path exists
      out = new XMLWriter(new FileWriter(file));
      out.write(document);
      out.flush();
      out.close();
    } catch (IOException ex) { //if path doesn't exit, create path
      new File(logPath + datePath).mkdirs();
      try {
        out = new XMLWriter(new FileWriter(file));
        out.write(document);
        out.flush();
        out.close();
      } catch (IOException _ex) {
        _ex.printStackTrace();
      }
    } 
	}

  /**
   * for debugging
   */
  public void printXML(Document document) {
    StringWriter strWriter = new StringWriter();
    OutputFormat format = OutputFormat.createPrettyPrint();
    format.setEncoding("UTF-8");
    XMLWriter xmlWriter = new XMLWriter(strWriter, format);
    try {
      xmlWriter.write(document);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    String strXML = strWriter.toString();
    System.out.println("XML for log:\n");
    System.out.println(strXML);
  }
}
