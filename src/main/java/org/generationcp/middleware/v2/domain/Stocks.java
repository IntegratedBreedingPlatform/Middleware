package org.generationcp.middleware.v2.domain;

import java.util.HashSet;
import java.util.Set;

public class Stocks {

	private Set<Stock> stocks = new HashSet<Stock>();
	
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
