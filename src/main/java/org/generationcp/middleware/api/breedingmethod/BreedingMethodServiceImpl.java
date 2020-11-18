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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public List<BreedingMethodDTO> getBreedingMethods(final String programUUID, final Set<String> abbreviations, final boolean favorites) {
		final List<Integer> breedingMethodIds = new ArrayList<>();
		if (!StringUtils.isEmpty(programUUID) && favorites) {
			breedingMethodIds.addAll(this.getFavoriteProjectMethodsIds(programUUID));
			if (breedingMethodIds.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
		}

		return this.daoFactory.getMethodDAO().filterMethods(programUUID, abbreviations, breedingMethodIds).stream()
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
