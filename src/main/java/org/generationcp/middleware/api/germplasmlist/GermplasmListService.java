package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public interface GermplasmListService {

	GermplasmListGeneratorDTO create(GermplasmListGeneratorDTO request, final int status, final String programUUID,
		final WorkbenchUser loggedInUser);
}
