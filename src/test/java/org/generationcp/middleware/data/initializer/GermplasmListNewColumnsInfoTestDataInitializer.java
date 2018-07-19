package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;

public class GermplasmListNewColumnsInfoTestDataInitializer {
	public static GermplasmListNewColumnsInfo createGermplasmListNewColumnsInfo() {
		GermplasmListNewColumnsInfo germplasmListNewColumnsInfo = new GermplasmListNewColumnsInfo(1);
		Map<String, List<ListDataColumnValues>> columnValuesMap = new HashMap<>();
		List<ListDataColumnValues> listDataColumnValues = new ArrayList<>();
		listDataColumnValues.add(new ListDataColumnValues("NOTE", 1, "Note 1"));
		columnValuesMap.put("NOTE", listDataColumnValues);
		germplasmListNewColumnsInfo.setColumnValuesMap(columnValuesMap);
		return germplasmListNewColumnsInfo;
	}
}
