package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.data.initializer.MethodTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class BreedingMethodServiceImplTest {

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	private BreedingMethodServiceImpl breedingMethodService;

	private DaoFactory daoFactory;
	private MethodDAO methodDAO;
	private ProgramFavoriteDAO programFavoriteDAO;

	@Captor
	private ArgumentCaptor<List<Integer>> listArgumentCaptor;

	@Before
	public void setUp() throws Exception {
		this.breedingMethodService = new BreedingMethodServiceImpl(null);

		this.methodDAO = Mockito.mock(MethodDAO.class);
		this.programFavoriteDAO = Mockito.mock(ProgramFavoriteDAO.class);

		this.daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(this.daoFactory.getMethodDAO()).thenReturn(this.methodDAO);
		Mockito.when(this.daoFactory.getProgramFavoriteDao()).thenReturn(this.programFavoriteDAO);
		ReflectionTestUtils.setField(this.breedingMethodService, "daoFactory", this.daoFactory);

		this.listArgumentCaptor = ArgumentCaptor.forClass(List.class);
	}

	@Test
	public void getBreedingMethods() {
		final List<Method> methods = new ArrayList<>();
		final Set<String> abbreviations = new HashSet<>();

		final Method method = MethodTestDataInitializer.createMethod();
		methods.add(method);

		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setProgramUUID(PROGRAM_UUID);
		Mockito.when(
			this.methodDAO.filterMethods(ArgumentMatchers.eq(searchRequest), ArgumentMatchers.eq(null)))
			.thenReturn(methods);

		final List<BreedingMethodDTO> breedingMethods = this.breedingMethodService.getBreedingMethods(searchRequest, null);
		assertNotNull(breedingMethods);
		assertThat(breedingMethods, hasSize(1));
		final BreedingMethodDTO actualBreedingMethodDTO = breedingMethods.get(0);
		this.assertBreedingMethodDTO(actualBreedingMethodDTO, method);

		Mockito.verify(this.methodDAO)
			.filterMethods(ArgumentMatchers.eq(searchRequest), ArgumentMatchers.eq(null));
	}

	@Test
	public void shouldGetBreedingMethodsFilteredByFavorites() {
		final List<Method> methods = new ArrayList<>();
		final Set<String> abbreviations = new HashSet<>();
		final Method method = MethodTestDataInitializer.createMethod();
		methods.add(method);

		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setProgramUUID(PROGRAM_UUID);
		searchRequest.setFavoritesOnly(true);
		Mockito.when(this.methodDAO.filterMethods(searchRequest, null))
			.thenReturn(methods);

		final ProgramFavorite programFavorite = new ProgramFavorite(PROGRAM_UUID, ProgramFavorite.FavoriteType.METHODS, method.getMid());
		Mockito.when(this.programFavoriteDAO.getProgramFavorites(ProgramFavorite.FavoriteType.METHODS, Integer.MAX_VALUE, PROGRAM_UUID))
			.thenReturn(Arrays.asList(programFavorite));

		final List<BreedingMethodDTO> breedingMethods = this.breedingMethodService.getBreedingMethods(searchRequest, null);
		assertNotNull(breedingMethods);
		assertThat(breedingMethods, hasSize(1));
		final BreedingMethodDTO actualBreedingMethodDTO = breedingMethods.get(0);
		this.assertBreedingMethodDTO(actualBreedingMethodDTO, method);

		Mockito.verify(this.methodDAO)
			.filterMethods(searchRequest, null);
		assertNotNull(searchRequest.getMethodIds());
		assertThat(searchRequest.getMethodIds(), hasSize(1));
		assertThat(searchRequest.getMethodIds().get(0), is(programFavorite.getEntityId()));

		Mockito.verify(this.programFavoriteDAO).getProgramFavorites(ProgramFavorite.FavoriteType.METHODS, Integer.MAX_VALUE, PROGRAM_UUID);
	}

	private void assertBreedingMethodDTO(final BreedingMethodDTO actualBreedingMethodDTO, final Method method) {
		assertThat(actualBreedingMethodDTO.getCode(), is(method.getMcode()));
		assertThat(actualBreedingMethodDTO.getDescription(), is(method.getMdesc()));
		assertThat(actualBreedingMethodDTO.getGroup(), is(method.getMgrp()));
		assertThat(actualBreedingMethodDTO.getMethodClass(), is(method.getGeneq()));
		assertThat(actualBreedingMethodDTO.getName(), is(method.getMname()));
		assertThat(actualBreedingMethodDTO.getType(), is(method.getMtype()));
	}

}
