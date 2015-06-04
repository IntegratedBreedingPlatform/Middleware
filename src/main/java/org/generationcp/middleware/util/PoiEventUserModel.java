/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.util;

import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class PoiEventUserModel {

	int maxLimit = 10000;

	public void areSheetRowsOverMaxLimit(String filename, int sheetIndex) throws Exception {
		this.areSheetRowsOverMaxLimit(filename, sheetIndex, this.maxLimit);
	}

	public void areSheetRowsOverMaxLimit(String filename, int sheetIndex, int maxLimit) throws Exception {

		this.maxLimit = maxLimit;

		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);

		XMLReader parser = this.fetchSheetParser();

		InputStream sheet2 = null;
		// rId2 found by processing the Workbook
		// Seems to either be rId# or rSheet#
		try {
			sheet2 = r.getSheet("rId" + (sheetIndex + 1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (sheet2 == null) {
			try {
				sheet2 = r.getSheet("rSheet" + (sheetIndex + 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		InputSource sheetSource = new InputSource(sheet2);
		parser.parse(sheetSource);
		if (sheet2 != null) {
			sheet2.close();
		}
	}

	public void isAnySheetRowsOverMaxLimit(String filename, int maxLimit) throws Exception {

		this.maxLimit = maxLimit;

		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);

		XMLReader parser = this.fetchSheetParser();

		for (int i = 1; i < 10; i++) {

			InputStream sheet = null;
			// rId2 found by processing the Workbook
			// Seems to either be rId# or rSheet#
			try {
				sheet = r.getSheet("rId" + i);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (sheet == null) {
				try {
					sheet = r.getSheet("rSheet" + i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (sheet != null) {
				InputSource sheetSource = new InputSource(sheet);
				parser.parse(sheetSource);
				sheet.close();
			} else {
				break;
			}

		}

	}

	public XMLReader fetchSheetParser() throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ContentHandler handler = new SheetHandler(this.maxLimit);
		parser.setContentHandler(handler);
		return parser;
	}

	/**
	 * See org.xml.sax.helpers.DefaultHandler javadocs
	 */
	private static class SheetHandler extends DefaultHandler {

		private int rowCounter = 0;
		private final int maxLimit;

		private SheetHandler(int maxLimit) {
			this.maxLimit = maxLimit;

		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {

			if (name.equals("row")) {
				if (this.rowCounter == this.maxLimit + 1) {
					throw new SAXException("You have exceeded the limit!!!!");
				}
				this.rowCounter++;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {

		}
	}

}
