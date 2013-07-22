/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.dms;

import java.util.LinkedHashSet;
import java.util.Set;

/** 
 * Set of stocks.
 */
public class Stocks {

	private Set<Stock> stocks = new LinkedHashSet<Stock>();
	
	public void add(Stock trialEnvironment) {
		if (trialEnvironment != null) {
			stocks.add(trialEnvironment);
		}
	}
	
	public Stock findOnlyOneByLocalName(String localName, String value) {
		Stock found = null;
		for (Stock stock : stocks) {
			if (stock.containsValueByLocalName(localName, value)) {
				if (found == null) found = stock;
				else { found = null; break; }
			}
		}
		return found;
	}
	
	public int countByLocalName(String localName, String value) {
		int count = 0;
		for (Stock stock : stocks) {
			if (stock.containsValueByLocalName(localName, value)) {
				count++;
			}
		}
		return count;
	}

	public void print(int indent) {
		for (Stock stock : stocks) {
			stock.print(indent);
		}
	}
}
