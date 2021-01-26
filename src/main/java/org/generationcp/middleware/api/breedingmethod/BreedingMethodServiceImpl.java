package org.generationcp.middleware.api.breedingmethod;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodClass;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class BreedingMethodServiceImpl implements BreedingMethodService {

	private final DaoFactory daoFactory;

	public BreedingMethodServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<MethodClassDTO> getMethodClasses() {
		final Map<MethodType, List<MethodClass>> methodClassMap = MethodClass.getByMethodType();

		final List<MethodClassDTO> classes = new ArrayList<>();
		for (final Map.Entry<MethodType, List<MethodClass>> entry : methodClassMap.entrySet()) {
			final List<Integer> termIds = entry.getValue().stream().map(MethodClass::getId).collect(Collectors.toList());
			final List<CVTerm> termList = this.daoFactory.getCvTermDao().getByIds(termIds);
			for (final CVTerm cvTerm : termList) {
				classes.add(new MethodClassDTO(cvTerm, entry.getKey()));
			}
		}
		return classes;
	}

	@Override
	public BreedingMethodDTO getBreedingMethod(final Integer breedingMethodDbId) {
		final Method methodEntity = this.daoFactory.getMethodDAO().getById(breedingMethodDbId);
		return new BreedingMethodDTO(methodEntity);
	}

	@Override
	public List<BreedingMethodDTO> getBreedingMethods(final BreedingMethodSearchRequest methodSearchRequest) {
		final String programUUID = methodSearchRequest.getProgramUUID();
		final boolean favoritesOnly = methodSearchRequest.isFavoritesOnly();
		if (!StringUtils.isEmpty(programUUID) && favoritesOnly) {
			final List<Integer> favoriteProjectMethodsIds = this.getFavoriteProjectMethodsIds(programUUID);
			// if filtering by program favorite methods but none exist, do not proceed with search and immediately return empty list
			if (CollectionUtils.isEmpty(favoriteProjectMethodsIds)) {
				return Collections.EMPTY_LIST;
			}
			methodSearchRequest.setMethodIds(favoriteProjectMethodsIds);
		}

		return this.daoFactory.getMethodDAO().filterMethods(methodSearchRequest).stream()
			.map(BreedingMethodDTO::new)
			.collect(Collectors.toList());
	}


	private List<Integer> getFavoriteProjectMethodsIds(final String programUUID) {
		return this.daoFactory.getProgramFavoriteDao()
			.getProgramFavorites(ProgramFavorite.FavoriteType.METHOD, Integer.MAX_VALUE, programUUID)
			.stream()
			.map(ProgramFavorite::getEntityId)
			.collect(Collectors.toList());
	}
}
