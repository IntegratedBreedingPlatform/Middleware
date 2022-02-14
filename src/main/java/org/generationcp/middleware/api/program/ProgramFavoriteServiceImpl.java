package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgramFavoriteServiceImpl implements ProgramFavoriteService {

	private final DaoFactory daoFactory;

	public ProgramFavoriteServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ProgramFavoriteDTO> addProgramFavorites(final String programUUID, final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds) {
		return entityIds.stream().map(entityId -> {
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(entityId);
			favorite.setEntityType(favoriteType);
			favorite.setUniqueID(programUUID);
			return this.daoFactory.getProgramFavoriteDao().save(favorite);
		}).map(ProgramFavoriteMapper.INSTANCE).collect(Collectors.toList());
	}

	@Override
	public void deleteAllProgramFavorites(final String programUUID) {
		this.daoFactory.getProgramFavoriteDao().deleteAllProgramFavorites(programUUID);
	}

	@Override
	public void deleteProgramFavorites(final String programUUID, final Set<Integer> programFavoriteIds) {
		this.daoFactory.getProgramFavoriteDao().deleteProgramFavorites(programUUID, programFavoriteIds);
	}

	@Override
	public List<ProgramFavorite> getProgramFavorites(final String programUUID, final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds) {
		return this.daoFactory.getProgramFavoriteDao().getProgramFavorites(programUUID, favoriteType, entityIds);
	}

	@Override
	public void deleteProgramFavorites(final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds) {
		this.daoFactory.getProgramFavoriteDao().deleteProgramFavorites(favoriteType, entityIds);
	}

}
