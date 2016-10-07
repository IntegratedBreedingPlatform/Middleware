
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;

public class ProgramFavoriteTestDataInitializer {

	public ProgramFavorite createProgramFavorite(final int entityId, final String programUUID) {
		final ProgramFavorite programFavorite = new ProgramFavorite();
		programFavorite.setEntityId(entityId);
		programFavorite.setEntityType(ProgramFavorite.FavoriteType.METHOD.getName());
		programFavorite.setUniqueID(programUUID);
		return programFavorite;
	}
}
