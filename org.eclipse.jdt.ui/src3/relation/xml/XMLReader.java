/*
 * @(#) ReadXML.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLReader {
	public static void main(String[] args) {
		String xmlfile = "./test5/security/adito-0.9.1/adito-community-applications/webapp/WEB-INF/adito-community-applications-struts-config.xml";
		// List<String> list = UtilFile.fileRead2List(xmlfile);
		// UtilPrint.printArrayList(list);
		XMLReader xmlReader = new XMLReader(xmlfile, true);
		List<String> parmList = new ArrayList<String>();
		parmList.add("form-bean");
		parmList.add("name");
		parmList.add("type");
		xmlReader.parse(parmList);
	}

	XMLHandler		_handler;
	String			_xmlfilePath;
	List<String>	_parmList;
	List<String>	_valList	= new ArrayList<String>();
	boolean			_debug;

	/** @CONSTRUCTOR */
	public XMLReader(String xmlfilePath) {
		_xmlfilePath = xmlfilePath;
	}

	public XMLReader(String xmlfilePath, boolean debug) {
		_xmlfilePath = xmlfilePath;
		_debug = debug;
	}

	/**
	 * @param parmList - contain parameters, including XML tag name,
	 *        the related attributes, and the corresponding program constructors.
	 */
	public void parse(List<String> parmList) {
		this._parmList = parmList;
		try {
			// SAXParserFactory factory = SAXParserFactory.newInstance();
			// SAXParser saxParser = factory.newSAXParser();
			// _handler = new XMLHandler(parmList, _debug);
			// saxParser.parse(_xmlfilePath, _handler);

			org.xml.sax.XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setEntityResolver(new DummyEntityResolver());
			_handler = new XMLHandler(parmList, _debug);
			xr.setContentHandler(_handler);
			xr.setErrorHandler(_handler);
			FileReader r = new FileReader(_xmlfilePath);
			xr.parse(new InputSource(r));

		} catch (Exception e) {
			System.out.println("[DBG]" + e);
		}
	}

	/** @METHOD */
	public List<String> getValues() {
		return _handler.getValues();
	}
}

class DummyEntityResolver implements EntityResolver {
	public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
		return new InputSource(new StringReader(""));
	}
}

/** XML Handler based on SAX */
class XMLHandler extends DefaultHandler {
	List<String>	_parmList	= new ArrayList<String>();
	List<String>	_valList		= new ArrayList<String>();
	boolean			_debug		= true;

	/** @METHOD */
	public XMLHandler(List<String> parm_list, boolean debug) {
		_parmList = parm_list;
		_debug = debug;
	}

	/** @METHOD */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase(_parmList.get(0))) {
			if (_debug)
				System.out.println("<" + qName + ">");
			for (int i = 1; i < _parmList.size(); i++) {
				String attr = _parmList.get(i).trim();
				String value = attributes.getValue(attr);
				if (value != null) {
					_valList.add(value);
					if (_debug)
						System.out.println(" * " + attr + ": " + value);
				}
			}
			// <<< Print >>>
			// for (int i = 0; i < attributes.getLength(); i++) {
			// String qname = attributes.getQName(i);
			// if (parm_list.contains(qname)) {
			// String value = attributes.getValue(qname);
			// System.out.println(" * " + qname + ": " + value);
			// }
			// }
		}
	}

	/** @METHOD */
	public List<String> getValues() {
		return _valList;
	}
}
