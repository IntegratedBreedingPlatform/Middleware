package org.generationcp.middleware.api.breedingmethod;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodClass;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

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
	public Optional<BreedingMethodDTO> getBreedingMethod(final Integer breedingMethodDbId) {
		final Method methodEntity = this.daoFactory.getMethodDAO().getById(breedingMethodDbId);

		final Optional<String> programOptional = ContextHolder.getCurrentProgramOptional();
		final Optional<ProgramFavorite> programFavorite = programOptional.isPresent()
			? this.daoFactory.getProgramFavoriteDao().getProgramFavorite(
			programOptional.get(), ProgramFavorite.FavoriteType.METHOD, breedingMethodDbId)
			: Optional.empty();

		if (!Objects.isNull(methodEntity)) {
			final BreedingMethodDTO breedingMethodDTO = new BreedingMethodDTO(methodEntity);
			breedingMethodDTO.setFavorite(programOptional.isPresent() ? programFavorite.isPresent() : null);
			return Optional.of(breedingMethodDTO);
		}
		return Optional.empty();
	}

	@Override
	public BreedingMethodDTO create(final BreedingMethodNewRequest breedingMethod) {
		final String name = breedingMethod.getName();
		final List<Method> methods = this.daoFactory.getMethodDAO().getByName(name);
		if (!isEmpty(methods)) {
			throw new MiddlewareRequestException("", "breeding.methods.name.exists", name);
		}
		final String code = breedingMethod.getCode().toUpperCase();
		final Method byCode = this.daoFactory.getMethodDAO().getByCode(code);
		if (byCode != null) {
			throw new MiddlewareRequestException("", "breeding.methods.code.exists", code);
		}

		final BreedingMethodMapper mapper = new BreedingMethodMapper();
		final Method method = new Method();
		mapper.map(breedingMethod, method);
		method.setUser(ContextHolder.getLoggedInUserId());
		method.setMdate(Util.getCurrentDateAsIntegerValue());

		final Method savedMethod = this.daoFactory.getMethodDAO().save(method);
		return new BreedingMethodDTO(savedMethod);
	}

	@Override
	public BreedingMethodDTO edit(final Integer breedingMethodDbId, final BreedingMethodNewRequest breedingMethod) {
		final Method method = this.daoFactory.getMethodDAO().getById(breedingMethodDbId);
		Preconditions.checkNotNull(method);

		final String name = breedingMethod.getName();
		if (!isBlank(name) && !name.equalsIgnoreCase(method.getMname())) {
			final List<Method> methods = this.daoFactory.getMethodDAO().getByName(name);
			if (!isEmpty(methods)) {
				throw new MiddlewareRequestException("", "breeding.methods.name.exists", name);
			}
		}
		final String code = breedingMethod.getCode().toUpperCase();
		if (!isBlank(code) && !code.equalsIgnoreCase(method.getMcode())) {
			final Method byCode = this.daoFactory.getMethodDAO().getByCode(code);
			if (byCode != null) {
				throw new MiddlewareRequestException("", "breeding.methods.code.exists", code);
			}
		}

		final BreedingMethodMapper mapper = new BreedingMethodMapper();
		mapper.mapForUpdate(breedingMethod, method);
		this.daoFactory.getMethodDAO().update(method);
		return new BreedingMethodDTO(method);
	}

	@Override
	public void delete(final Integer breedingMethodDbId) {
		final Method method = this.daoFactory.getMethodDAO().getById(breedingMethodDbId);
		this.daoFactory.getMethodDAO().makeTransient(method);
	}

	@Override
	public List<BreedingMethodDTO> getBreedingMethods(final BreedingMethodSearchRequest methodSearchRequest, final Pageable pageable) {
		final String programUUID = methodSearchRequest.getProgramUUID();
		final boolean favoritesOnly = methodSearchRequest.isFavoritesOnly();

		final List<Integer> favoriteProjectMethodsIds = this.getFavoriteProjectMethodsIds(programUUID);
		final HashSet<Integer> favoriteIdsSet = new HashSet<>(favoriteProjectMethodsIds);

		if (!StringUtils.isEmpty(programUUID) && favoritesOnly) {
			// if filtering by program favorite methods but none exist, do not proceed with search and immediately return empty list
			if (isEmpty(favoriteProjectMethodsIds)) {
				return Collections.emptyList();
			}
			methodSearchRequest.setMethodIds(favoriteProjectMethodsIds);
		}

		return this.daoFactory.getMethodDAO().filterMethods(methodSearchRequest, pageable).stream()
			.map(method -> {
				final BreedingMethodDTO breedingMethodDTO = new BreedingMethodDTO(method);
				breedingMethodDTO.setFavorite(favoriteIdsSet.contains(method.getMid()));
				return breedingMethodDTO;
			})
			.collect(Collectors.toList());
	}

	@Override
	public Long countBreedingMethods(final BreedingMethodSearchRequest methodSearchRequest) {
		return this.daoFactory.getMethodDAO().countFilteredMethods(methodSearchRequest);
	}

	private List<Integer> getFavoriteProjectMethodsIds(final String programUUID) {
		return this.daoFactory.getProgramFavoriteDao()
			.getProgramFavorites(ProgramFavorite.FavoriteType.METHOD, Integer.MAX_VALUE, programUUID)
			.stream()
			.map(ProgramFavorite::getEntityId)
			.collect(Collectors.toList());
	}
}
