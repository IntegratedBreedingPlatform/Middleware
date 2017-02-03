package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;

public class ListDataProjectTestDataInitializer {

	public static ListDataProject createListDataProject(final GermplasmList germplasmList, final Integer gid, final Integer checkType,
			final Integer entryId, final String entryCode, final String seedSource, final String designation, final String groupName,
			final String duplicate, final String notes, final Integer crossingDate) {

		ListDataProject listDataProject = new ListDataProject(germplasmList, gid, checkType, entryId, entryCode, seedSource, designation,
				groupName, duplicate, notes, crossingDate);

		return listDataProject;

	}
}
