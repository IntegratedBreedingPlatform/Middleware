package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

public class GermplasmListDataTestDataInitializer {
	
	public GermplasmListData createGermplasmListData(GermplasmList germplasmList, Integer gid, Integer entryId){
		GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entryId, "EntryCode", "SeedSource",
				"Germplasm Name 5", "GroupName", 0, 99995);
		return germplasmListData;
	}
}
