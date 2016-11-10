import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.xml.sax.InputSource;

public class XMLHandler {
	public XMLHandler() {
	}

	public String createXML() {
		String strXML = null;
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");

		Element phone = root.addElement("telephone");

		Element nokia = phone.addElement("type");
		nokia.addAttribute("name", "nokia");
		Element price_nokia = nokia.addElement("price");
		price_nokia.addText("599");
		Element operator_nokia = nokia.addElement("operator");
		operator_nokia.addText("CMCC");

		Element xiaomi = phone.addElement("type");
		xiaomi.addAttribute("name", "xiaomi");
		Element price_xiaomi = xiaomi.addElement("price");
		price_xiaomi.addText("699");
		Element operator_xiaomi = xiaomi.addElement("operator");
		operator_xiaomi.addText("ChinaNet");

		//----
		StringWriter strWriter = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWriter, format);
		try {
			xmlWriter.write(document);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		strXML = strWriter.toString();
		//----

		//----
		//strXML = document.asXML();
		//----

		//----
		File file = new File("telephone.xml");
		if (file.exists()) 
			file.delete();
		try {
			file.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(file));
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return strXML;
	}

	public void parseXML(String strXML) {
		SAXReader reader = new SAXReader();
		StringReader sr = new StringReader(strXML);
		InputSource is = new InputSource(sr);
		try {
			Document document = reader.read(is);
			
			Element root = document.getRootElement();

			List<Element> phones = root.elements("telephone");
			List<Element> types = phones.get(0).elements("type");
			for (Element type : types) {
				String phoneName = type.attributeValue("name");
				System.out.println("phonename="+phoneName);

				List<Element> childList = type.elements();
				for (Element child : childList) 
					System.out.println(child.getName()+"="+child.getText());
			}
		} catch (DocumentException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		XMLHandler handler = new XMLHandler();
		String strXML = handler.createXML();
		System.out.println(strXML);
		handler.parseXML(strXML);
	}
}
