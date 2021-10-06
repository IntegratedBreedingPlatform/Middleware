package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProgramFavoriteServiceImpl implements ProgramFavoriteService {

	private final DaoFactory daoFactory;

	public ProgramFavoriteServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void addProgramFavorites(final String programUUID, final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds) {
		entityIds.forEach(entityId -> {
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(entityId);
			favorite.setEntityType(favoriteType.getName());
			favorite.setUniqueID(programUUID);
			this.daoFactory.getProgramFavoriteDao().save(favorite);
		});
	}

	@Override
	public void deleteAllProgramFavorites(final String programUUID) {
		this.daoFactory.getProgramFavoriteDao().deleteAllProgramFavorites(programUUID);
	}

}
