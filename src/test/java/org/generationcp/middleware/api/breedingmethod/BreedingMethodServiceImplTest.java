package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.data.initializer.MethodTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
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
	public void shouldSearchBreedingMethods() {
		final List<BreedingMethodDTO> methods = new ArrayList<>();

		final Method method = MethodTestDataInitializer.createMethod();
		methods.add(new BreedingMethodDTO(method));

		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setFavoriteProgramUUID(PROGRAM_UUID);
		Mockito.when(
			this.methodDAO.searchBreedingMethods(ArgumentMatchers.eq(searchRequest), ArgumentMatchers.isNull(), ArgumentMatchers.isNull()))
			.thenReturn(methods);

		final List<BreedingMethodDTO> breedingMethods = this.breedingMethodService.searchBreedingMethods(searchRequest, null, null);
		assertNotNull(breedingMethods);
		assertThat(breedingMethods, hasSize(1));
		final BreedingMethodDTO actualBreedingMethodDTO = breedingMethods.get(0);
		this.assertBreedingMethodDTO(actualBreedingMethodDTO, method);

		Mockito.verify(this.methodDAO)
			.searchBreedingMethods(ArgumentMatchers.eq(searchRequest), ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
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
