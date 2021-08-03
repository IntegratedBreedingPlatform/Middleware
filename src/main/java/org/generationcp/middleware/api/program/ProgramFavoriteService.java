package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;

public interface ProgramFavoriteService {

	void addProgramFavorite(String programUUID, ProgramFavorite.FavoriteType favoriteType, Integer entityId);

}
