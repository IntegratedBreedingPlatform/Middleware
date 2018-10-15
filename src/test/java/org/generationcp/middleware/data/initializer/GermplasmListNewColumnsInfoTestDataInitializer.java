package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;

public class GermplasmListNewColumnsInfoTestDataInitializer {

	public static GermplasmListNewColumnsInfo createGermplasmListNewColumnsInfo() {
		return GermplasmListNewColumnsInfoTestDataInitializer.createGermplasmListNewColumnsInfo("NOTE", "Note 1");
	}

	public static GermplasmListNewColumnsInfo createGermplasmListNewColumnsInfo(final String columnHeader, final String columnValue) {
		final GermplasmListNewColumnsInfo germplasmListNewColumnsInfo = new GermplasmListNewColumnsInfo(1);
		final Map<String, List<ListDataColumnValues>> columnValuesMap = new HashMap<>();
		final List<ListDataColumnValues> listDataColumnValues = new ArrayList<>();
		listDataColumnValues.add(new ListDataColumnValues(columnHeader, 1, columnValue));
		columnValuesMap.put(columnHeader, listDataColumnValues);
		germplasmListNewColumnsInfo.setColumnValuesMap(columnValuesMap);
		return germplasmListNewColumnsInfo;
	}
}
