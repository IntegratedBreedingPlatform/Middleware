package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;

import java.util.List;
import java.util.Set;

public interface ProgramFavoriteService {

	void addProgramFavorites(String programUUID, ProgramFavorite.FavoriteType favoriteType, Set<Integer> entityIds);

	void deleteAllProgramFavorites(String programUUID);

}
