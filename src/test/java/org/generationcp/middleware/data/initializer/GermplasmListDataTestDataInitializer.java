
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

public class GermplasmListDataTestDataInitializer {

	public GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final Integer gid, final Integer entryId) {
		final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entryId, "EntryCode", "SeedSource",
				"Germplasm Name 5", "GroupName", 0, 99995);
		return germplasmListData;
	}
}
