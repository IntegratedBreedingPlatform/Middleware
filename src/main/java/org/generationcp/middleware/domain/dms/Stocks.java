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

package org.generationcp.middleware.domain.dms;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Set of stocks.
 */
public class Stocks {

	private final Set<Stock> stocks = new LinkedHashSet<Stock>();

	public void add(final Stock trialEnvironment) {
		if (trialEnvironment != null) {
			this.stocks.add(trialEnvironment);
		}
	}

	public Stock findOnlyOneByLocalName(final String localName, final String value) {
		Stock found = null;
		for (final Stock stock : this.stocks) {
			if (stock.containsValueByLocalName(localName, value)) {
				if (found == null) {
					found = stock;
				} else {
					found = null;
					break;
				}
			}
		}
		return found;
	}

	public int countByLocalName(final String localName, final String value) {
		int count = 0;
		for (final Stock stock : this.stocks) {
			if (stock.containsValueByLocalName(localName, value)) {
				count++;
			}
		}
		return count;
	}

	public void print(final int indent) {
		for (final Stock stock : this.stocks) {
			stock.print(indent);
		}
	}

	public Map<String, Integer> getStockMap(final String keyVarName) {
		final Map<String, Integer> stockMap = new LinkedHashMap<>();
		for (final Stock stock : this.stocks) {
			final Variable variableKey = stock.getVariables().findByLocalName(keyVarName);
			stockMap.put(variableKey.getValue(), stock.getId());
		}
		return stockMap;
	}
}
