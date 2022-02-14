package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;

import java.util.List;
import java.util.Set;

public interface ProgramFavoriteService {

	List<ProgramFavoriteDTO> addProgramFavorites(String programUUID, ProgramFavorite.FavoriteType favoriteType, Set<Integer> entityIds);

	void deleteAllProgramFavorites(String programUUID);

	void deleteProgramFavorites(String programUUID, Set<Integer>  programFavoriteIds);

	List<ProgramFavorite> getProgramFavorites(String programUUID, ProgramFavorite.FavoriteType favoriteType, Set<Integer> entityIds);

	void deleteProgramFavorites(ProgramFavorite.FavoriteType favoriteType, Set<Integer>  entityIds);
}
