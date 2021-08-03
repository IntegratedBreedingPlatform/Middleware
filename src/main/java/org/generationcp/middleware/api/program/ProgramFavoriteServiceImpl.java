package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProgramFavoriteServiceImpl implements ProgramFavoriteService {

	private final DaoFactory daoFactory;

	public ProgramFavoriteServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void addProgramFavorite(final String programUUID, final ProgramFavorite.FavoriteType favoriteType, final Integer entityId) {
		final ProgramFavorite favorite = new ProgramFavorite();
		favorite.setEntityId(entityId);
		favorite.setEntityType(favoriteType.getName());
		favorite.setUniqueID(programUUID);
		this.daoFactory.getProgramFavoriteDao().save(favorite);
	}

}
